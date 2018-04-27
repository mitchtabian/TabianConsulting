package courses.pluralsight.com.tabianconsulting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import courses.pluralsight.com.tabianconsulting.models.ChatMessage;
import courses.pluralsight.com.tabianconsulting.models.Chatroom;
import courses.pluralsight.com.tabianconsulting.models.User;
import courses.pluralsight.com.tabianconsulting.utility.ChatroomListAdapter;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    //widgets
    private ListView mListView;
    private FloatingActionButton mFob;

    //vars
    private ArrayList<Chatroom> mChatrooms;
    private ChatroomListAdapter mAdapter;
    private int mSecurityLevel;
    private DatabaseReference mChatroomReference;
    public static boolean isActivityRunning;
    private HashMap<String, String> mNumChatroomMessages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mListView = (ListView) findViewById(R.id.listView);
        mFob = (FloatingActionButton) findViewById(R.id.fob);

        init();
    }

    public void init(){
        mChatrooms = new ArrayList<>();
        getUserSecurityLevel();
        mFob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewChatroomDialog dialog = new NewChatroomDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_chatroom));
            }
        });

    }

    private void setupChatroomList() {
        Log.d(TAG, "setupChatroomList: setting up chatroom listview");
        mAdapter = new ChatroomListAdapter(ChatActivity.this, R.layout.layout_chatroom_listitem, mChatrooms);
        mListView.setAdapter(mAdapter);
    }

    /**
     * Join a chatroom selected by the user.
     * This method is executed from the ChatroomListAdapter class
     * This method checks to make sure the chatroom exists before joining.
     * @param chatroom
     */
    public void joinChatroom(final Chatroom chatroom){
        //make sure the chatroom exists before joining
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbnode_chatrooms)).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:  dataSnapshot.getChildren()){
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    if(objectMap.get(getString(R.string.field_chatroom_id)).toString()
                            .equals(chatroom.getChatroom_id())){
                        if(mSecurityLevel >= Integer.parseInt(chatroom.getSecurity_level())){
                            Log.d(TAG, "onItemClick: selected chatroom: " + chatroom.getChatroom_id());

                            //add user to the list of users who have joined the chatroom
                            addUserToChatroom(chatroom);

                            //navigate to the chatoom
                            Intent intent = new Intent(ChatActivity.this, ChatroomActivity.class);
                            intent.putExtra(getString(R.string.intent_chatroom), chatroom);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ChatActivity.this, "insufficient security level", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                getChatrooms();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * add the current user to the list of users who have joined the chatroom.
     * Users who have joined the chatroom will receive notifications on chatroom activity.
     * They will receive notifications via a cloud functions sending a cloud message to the
     * chatroom ID (Sending via topic FCM)
     * @param chatroom
     */
    private void addUserToChatroom(Chatroom chatroom){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbnode_chatrooms))
                .child(chatroom.getChatroom_id())
                .child(getString(R.string.field_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_last_message_seen))
                .setValue(mNumChatroomMessages.get(chatroom.getChatroom_id()));

    }

    public void getChatrooms(){
        Log.d(TAG, "getChatrooms: retrieving chatrooms from firebase database.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        mNumChatroomMessages = new HashMap<>();
        if(mAdapter != null){
            mAdapter.clear();
            mChatrooms.clear();
        }
        Query query = reference.child(getString(R.string.dbnode_chatrooms)).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot:  dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: found chatroom: "
//                            + singleSnapshot.getValue());
                    try{
                        if(singleSnapshot.exists()){
                            Chatroom chatroom = new Chatroom();
                            Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                            Log.d(TAG, "onDataChange: found a chatroom: "
                                    + objectMap.get(getString(R.string.field_chatroom_name)).toString());
                            chatroom.setChatroom_id(objectMap.get(getString(R.string.field_chatroom_id)).toString());
                            chatroom.setChatroom_name(objectMap.get(getString(R.string.field_chatroom_name)).toString());
                            chatroom.setCreator_id(objectMap.get(getString(R.string.field_creator_id)).toString());
                            chatroom.setSecurity_level(objectMap.get(getString(R.string.field_security_level)).toString());


    //                    chatroom.setChatroom_id(singleSnapshot.getValue(Chatroom.class).getChatroom_id());
    //                    chatroom.setSecurity_level(singleSnapshot.getValue(Chatroom.class).getSecurity_level());
    //                    chatroom.setCreator_id(singleSnapshot.getValue(Chatroom.class).getCreator_id());
    //                    chatroom.setChatroom_name(singleSnapshot.getValue(Chatroom.class).getChatroom_name());

                            //get the chatrooms messages
                            ArrayList<ChatMessage> messagesList = new ArrayList<ChatMessage>();
                            int numMessages = 0;
                            for(DataSnapshot snapshot: singleSnapshot
                                    .child(getString(R.string.field_chatroom_messages)).getChildren()){
                                ChatMessage message = new ChatMessage();
                                message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                message.setUser_id(snapshot.getValue(ChatMessage.class).getUser_id());
                                message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                messagesList.add(message);
                                numMessages++;
                            }
                            if(messagesList.size() > 0){
                                chatroom.setChatroom_messages(messagesList);

                                //add the number of chatrooms messages to a hashmap for reference
                                mNumChatroomMessages.put(chatroom.getChatroom_id(), String.valueOf(numMessages));
                            }

                            //get the list of users who have joined the chatroom
                            List<String> users = new ArrayList<String>();
                            for(DataSnapshot snapshot: singleSnapshot
                                    .child(getString(R.string.field_users)).getChildren()){
                                String user_id = snapshot.getKey();
                                Log.d(TAG, "onDataChange: user currently in chatroom: " + user_id);
                                users.add(user_id);
                            }
                            if(users.size() > 0){
                                chatroom.setUsers(users);
                            }

                            mChatrooms.add(chatroom);
                        }

                        setupChatroomList();
                    }catch (NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage() );
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserSecurityLevel(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbnode_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datasnapshot: " + dataSnapshot);
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                int securityLevel = Integer.parseInt(singleSnapshot.getValue(User.class).getSecurity_level());
                Log.d(TAG, "onDataChange: user has a security level of: " + securityLevel);
                mSecurityLevel = securityLevel;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showDeleteChatroomDialog(String chatroomId){
        DeleteChatroomDialog dialog = new DeleteChatroomDialog();
        Bundle args = new Bundle();
        args.putString(getString(R.string.field_chatroom_id), chatroomId);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_delete_chatroom));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called.");
        checkAuthenticationState();
        getChatrooms();
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    /**
     * Return the current timestamp in the form of a string
     * @return
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}












