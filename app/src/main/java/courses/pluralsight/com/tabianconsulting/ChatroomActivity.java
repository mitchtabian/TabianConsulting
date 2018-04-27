package courses.pluralsight.com.tabianconsulting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import courses.pluralsight.com.tabianconsulting.models.ChatMessage;
import courses.pluralsight.com.tabianconsulting.models.Chatroom;
import courses.pluralsight.com.tabianconsulting.models.User;
import courses.pluralsight.com.tabianconsulting.utility.ChatMessageListAdapter;



public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mMessagesReference;

    //widgets
    private TextView mChatroomName;
    private ListView mListView;
    private EditText mMessage;
    private ImageView mCheckmark;

    //vars
    private Chatroom mChatroom;
    private List<ChatMessage> mMessagesList;
    private Set<String> mMessageIdSet;
    private ChatMessageListAdapter mAdapter;
    public static boolean isActivityRunning;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        mChatroomName = (TextView) findViewById(R.id.text_chatroom_name);
        mListView = (ListView) findViewById(R.id.listView);
        mMessage = (EditText) findViewById(R.id.input_message);
        mCheckmark = (ImageView) findViewById(R.id.checkmark);
        getSupportActionBar().hide();
        Log.d(TAG, "onCreate: started.");

        setupFirebaseAuth();
        getChatroom();
        init();
        hideSoftKeyboard();
    }

    private void init(){

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
            }
        });

        mCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mMessage.getText().toString().equals("")){
                    String message = mMessage.getText().toString();
                    Log.d(TAG, "onClick: sending new message: " + message);

                    //create the new message object for inserting
                    ChatMessage newMessage = new ChatMessage();
                    newMessage.setMessage(message);
                    newMessage.setTimestamp(getTimestamp());
                    newMessage.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    //get a database reference
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbnode_chatrooms))
                            .child(mChatroom.getChatroom_id())
                            .child(getString(R.string.field_chatroom_messages));

                    //create the new messages id
                    String newMessageId = reference.push().getKey();

                    //insert the new message into the chatroom
                    reference
                            .child(newMessageId)
                            .setValue(newMessage);

                    //clear the EditText
                    mMessage.setText("");

                    //refresh the messages list? Or is it done by the listener??
                }

            }
        });
    }

    /**
     * Retrieve the chatroom name using a query
     */
    private void getChatroom(){
        Log.d(TAG, "getChatroom: getting selected chatroom details");

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_chatroom))){
            Chatroom chatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom));
            Log.d(TAG, "getChatroom: chatroom: " + chatroom.toString());
            mChatroom = chatroom;
            mChatroomName.setText(mChatroom.getChatroom_name());

            enableChatroomListener();
        }
    }


    private void getChatroomMessages(){

        if(mMessagesList == null){
            mMessagesList = new ArrayList<>();
            mMessageIdSet = new HashSet<>();
            initMessagesList();
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_chatroom_messages));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: found chatroom message: "
                            + snapshot.getValue());
                    try {//need to catch null pointer here because the initial welcome message to the
                        //chatroom has no user id
                        ChatMessage message = new ChatMessage();
                        String userId = snapshot.getValue(ChatMessage.class).getUser_id();

                        //check to see if the message has already been added to the list
                        //if the message has already been added we don't need to add it again
                        if(!mMessageIdSet.contains(snapshot.getKey())){
                            Log.d(TAG, "onDataChange: adding a new message to the list: " + snapshot.getKey());
                            //add the message id to the message set
                            mMessageIdSet.add(snapshot.getKey());
                            if(userId != null){ //check and make sure it's not the first message (has no user id)
                                message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                message.setUser_id(snapshot.getValue(ChatMessage.class).getUser_id());
                                message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                message.setProfile_image("");
                                message.setName("");
                                mMessagesList.add(message);
                            }else{
                                message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                message.setProfile_image("");
                                message.setName("");
                                mMessagesList.add(message);
                            }
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage());
                    }
                }
                //query the users node to get the profile images and names
                getUserDetails();
                mAdapter.notifyDataSetChanged(); //notify the adapter that the dataset has changed
                mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
                //initMessagesList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mMessagesList.size(); i++) {
           // Log.d(TAG, "onDataChange: searching for userId: " + mMessagesList.get(i).getUser_id());
            final int j = i;
            if(mMessagesList.get(i).getUser_id() != null && mMessagesList.get(i).getProfile_image().equals("")){
                Query query = reference.child(getString(R.string.dbnode_users))
                        .orderByKey()
                        .equalTo(mMessagesList.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                        mMessagesList.get(j).setProfile_image(singleSnapshot.getValue(User.class).getProfile_image());
                        mMessagesList.get(j).setName(singleSnapshot.getValue(User.class).getName());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

    }


    private void initMessagesList(){
        mAdapter = new ChatMessageListAdapter(ChatroomActivity.this,
                R.layout.layout_chatmessage_listitem, mMessagesList);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
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

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
    */

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(ChatroomActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(ChatroomActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChatroomActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };

    }

    /**
     * upadte the total number of message the user has seen
     */
    private void updateNumMessages(int numMessages){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference
                .child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_last_message_seen))
                .setValue(String.valueOf(numMessages));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMessagesReference.removeEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getChatroomMessages();

            //get the number of messages currently in the chat and update the database
            int numMessages = (int) dataSnapshot.getChildrenCount();
            updateNumMessages(numMessages);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void enableChatroomListener(){
         /*
            ---------- Listener that will watch the 'chatroom_messages' node ----------
         */
        mMessagesReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_chatroom_messages));

        mMessagesReference.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }
}






















