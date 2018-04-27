package courses.pluralsight.com.tabianconsulting;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DeleteChatroomDialog extends DialogFragment {

    private static final String TAG = "DeleteChatroomDialog";

    //create a new bundle and set the arguments to avoid a null pointer
    public DeleteChatroomDialog(){
        super();
        setArguments(new Bundle());
    }

    private String mChatroomId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        mChatroomId = getArguments().getString(getString(R.string.field_chatroom_id));
        if(mChatroomId != null){
            Log.d(TAG, "onCreate: got the chatroom id: " + mChatroomId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_chatroom, container, false);


        TextView delete = (TextView) view.findViewById(R.id.confirm_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatroomId != null){
                    Log.d(TAG, "onClick: deleting chatroom: " + mChatroomId);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(getString(R.string.dbnode_chatrooms))
                            .child(mChatroomId)
                            .removeValue();
                    ((ChatActivity)getActivity()).getChatrooms();
                    getDialog().dismiss();
                }
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cenceling deletion of chatroom");
                getDialog().dismiss();
            }
        });


        return view;
    }

}

















