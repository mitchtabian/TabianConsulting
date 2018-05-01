package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import courses.pluralsight.com.tabianconsulting.R;


/**
 * Created by User on 4/16/2018.
 */

public class NewProjectActivity extends AppCompatActivity implements
        View.OnClickListener
{

    private static final String TAG = "NewProjectActivity";


    //widgets
    private EditText mProjectName, mProjectDescription;
    private TextView mCreate;
    private ImageView mClose;
    private ProgressBar mProgressBar;

    //vars


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        mProjectName = findViewById(R.id.project_name);
        mProjectDescription = findViewById(R.id.project_description);
        mCreate = findViewById(R.id.create);
        mProgressBar = findViewById(R.id.progress_bar);
        mClose = findViewById(R.id.close);

        mCreate.setOnClickListener(this);
        mClose.setOnClickListener(this);

        setupActionBar();
    }

    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void createNewProject(){
        String projectName = mProjectName.getText().toString();
        String projectDescription = mProjectDescription.getText().toString();

        if(mProjectName.equals("") || mProjectDescription.equals("")){
            Toast.makeText(this, "fill in the fields", Toast.LENGTH_SHORT).show();
        }
        else{

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
                createNewProject();
                break;
            }

            case R.id.close:{
                finish();
                break;
            }
        }
    }

}

















