package courses.pluralsight.com.tabianconsulting.issues;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;


/**
 * Created by User on 4/16/2018.
 */

public class ProjectsFragment extends Fragment implements
        View.OnClickListener
{

    private static final String TAG = "ProjectsFragment";

    //widgets
    private ImageView mAddIcon;

    //vars
    private IIssues mIIssues;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        mAddIcon = view.findViewById(R.id.add_new);

        mAddIcon.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()){

            case R.id.add_new:{
                //go to NewProjectActivity
                intent = new Intent(getActivity(), NewProjectActivity.class);
                startActivityForResult(intent, ResultCodes.SNACKBAR_RESULT_CODE);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == ResultCodes.SNACKBAR_RESULT_CODE){
            Log.d(TAG, "onActivityResult: building snackbar message.");
            String message = data.getStringExtra(getString(R.string.intent_snackbar_message));
            mIIssues.buildSnackbar(message);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mIIssues = (IIssues) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: Class Cast Exception: " + e.getMessage() );
        }
    }
}
















