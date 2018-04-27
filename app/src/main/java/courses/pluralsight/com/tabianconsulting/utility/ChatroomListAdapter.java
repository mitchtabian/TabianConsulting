package courses.pluralsight.com.tabianconsulting.utility;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import courses.pluralsight.com.tabianconsulting.ChatActivity;
import courses.pluralsight.com.tabianconsulting.DeleteChatroomDialog;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Chatroom;
import courses.pluralsight.com.tabianconsulting.models.User;

/**
 * Created by User on 9/18/2017.
 */

public class ChatroomListAdapter extends ArrayAdapter<Chatroom> {

    private static final String TAG = "ChatroomListAdapter";

    private int mLayoutResource;
    private Context mContext;
    private LayoutInflater mInflater;

    public ChatroomListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Chatroom> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder{
        TextView name, creatorName, numberMessages;
        ImageView mProfileImage, mTrash;
        Button leaveChat;
        RelativeLayout layoutContainer;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.creatorName = (TextView) convertView.findViewById(R.id.creator_name);
            holder.numberMessages = (TextView) convertView.findViewById(R.id.number_chatmessages);
            holder.mProfileImage = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.mTrash = (ImageView) convertView.findViewById(R.id.icon_trash);
            holder.leaveChat = (Button) convertView.findViewById(R.id.leave_chat);
            holder.layoutContainer = (RelativeLayout) convertView.findViewById(R.id.layout_container);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        try{
            //set the chatroom name
            holder.name.setText(getItem(position).getChatroom_name());

            //set the number of chat messages
            String chatMessagesString = String.valueOf(getItem(position).getChatroom_messages().size())
                    + " messages";
            holder.numberMessages.setText(chatMessagesString);

            //get the users details who created the chatroom
            Query query = reference.child(mContext.getString(R.string.dbnode_users))
                    .orderByKey()
                    .equalTo(getItem(position).getCreator_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot:  dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: Found chat room creator: "
                                + singleSnapshot.getValue(User.class).getName());
                        String createdBy = "created by " + singleSnapshot.getValue(User.class).getName();
                        holder.creatorName.setText(createdBy);
                        ImageLoader.getInstance().displayImage(
                                singleSnapshot.getValue(User.class).getProfile_image() , holder.mProfileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            holder.mTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getItem(position).getCreator_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Log.d(TAG, "onClick: asking for permission to delete icon.");
                        ((ChatActivity)mContext).showDeleteChatroomDialog(getItem(position).getChatroom_id());
                    }else{
                        Toast.makeText(mContext, "You didn't create this chatroom", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.layoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: navigating to chatroom");
                    ((ChatActivity)mContext).joinChatroom(getItem(position));
                }
            });
            /*
            -------- Check if user is part of this chatroom --------
                1) if they are: give them ability to leave it
                2) if they aren't: hide the leave button
            */
            List<String> usersInChatroom = getItem(position).getUsers();
            if(usersInChatroom.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.leaveChat.setVisibility(View.VISIBLE);

                holder.leaveChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: leaving chatroom with id: " + getItem(position).getChatroom_id());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(mContext.getString(R.string.dbnode_chatrooms))
                                .child(getItem(position).getChatroom_id())
                                .child(mContext.getString(R.string.field_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                        holder.leaveChat.setVisibility(View.GONE);
                    }
                });
            }





        }catch (NullPointerException e){
            Log.e(TAG, "getView: NullPointerException: ", e.getCause() );
        }

        return convertView;
    }
}

















