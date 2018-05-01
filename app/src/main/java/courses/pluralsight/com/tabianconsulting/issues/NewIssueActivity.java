package courses.pluralsight.com.tabianconsulting.issues;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.ChangePhotoDialog;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.utility.SpinnerResource;


/**
 * Created by User on 4/17/2018.
 */

public class NewIssueActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "NewIssueActivity";
    private static final int REQUEST_CODE = 1234;

    //widgets
    private Spinner mIssueTypeSpinner, mPrioritySpinner;
    private AutoCompleteTextView mAssignToProject;
    private EditText mSummary, mDescription;
    private ImageView mClose;
    private TextView mCreate;
    private ProgressBar mProgressBar;


    //vars
    private boolean mStoragePermissions;
    private ArrayList<Project> mProjects = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);
        mIssueTypeSpinner = findViewById(R.id.issue_type_spinner);
        mPrioritySpinner = findViewById(R.id.priority_spinner);
        mAssignToProject = findViewById(R.id.assign_to_project);
        mClose = findViewById(R.id.close);
        mCreate = findViewById(R.id.create);
        mSummary = findViewById(R.id.issue_summary);
        mDescription = findViewById(R.id.issue_description);
        mProgressBar = findViewById(R.id.progress_bar);

        mClose.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        setupActionBar();
        initIssueTypeSpinner();
        initPrioritySpinner();
    }

    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    private void initIssueTypeSpinner(){
        final String[] issueTypes = SpinnerResource.issue_type_spinner;
        int[] issueImages = SpinnerResource.issue_type_images_spinner;

        final SpinnerAdapter adapter = new SpinnerAdapter(this, issueTypes, issueImages);
        mIssueTypeSpinner.setAdapter(adapter);

        mIssueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedText(issueTypes[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initPrioritySpinner(){
        final String[] priorityLevels = SpinnerResource.issue_priorities_spinner;
        int[] priorityImages = SpinnerResource.issue_priority_images_spinner;

        final SpinnerAdapter adapter = new SpinnerAdapter(this, priorityLevels, priorityImages);
        mPrioritySpinner.setAdapter(adapter);

        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedText(priorityLevels[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void createNewIssue(){

        hideSoftKeyboard();

        if(mAssignToProject.getText().toString().equals("")){
            mAssignToProject.setError(getString(R.string.select_a_project));
        }
        else if(mSummary.getText().toString().equals("")){
            mSummary.setError(getString(R.string.required));
        }
        else{
            showProgressBar();

            // Find the Project id
            String temp = "";
            for(Project project : mProjects){
                if(project.getName().equals(mAssignToProject.getText().toString())){
                    temp = project.getProject_id();
                    break;
                }
            }
            final String projectId = temp;

            if(projectId.equals("")){
                Toast.makeText(this, "select a valid project", Toast.LENGTH_SHORT).show();
                mAssignToProject.setError(getString(R.string.select_a_project));
                hideProgressBar();
            }
            else{

            }

        }
    }



    private void showProgressBar(){
        if(mProgressBar != null){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        if(mProgressBar != null){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.create:{
                    createNewIssue();
                break;
            }

            case R.id.close:{
                finish();
                break;
            }
        }
    }


    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}





















