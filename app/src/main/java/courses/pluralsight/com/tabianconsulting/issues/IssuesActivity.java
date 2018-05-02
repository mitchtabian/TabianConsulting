package courses.pluralsight.com.tabianconsulting.issues;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;

/**
 * Created by User on 4/16/2018.
 */

public class IssuesActivity extends AppCompatActivity implements IIssues {

    private static final String TAG = "IssuesActivity";

    private static final int ISSUES_FRAGMENT = 0;
    private static final int PROJECTS_FRAGMENT = 1;

    //widgets
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;

    //vars
    private IssuesFragment mIssuesFragment;
    private ProjectsFragment mProjectsFragment;
    private IssuesPagerAdapter mIssuesPagerAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        mViewPager = findViewById(R.id.main_container);
        mProgressBar = findViewById(R.id.progress_bar);

        setupActionBar();
        setupViewPager();
    }


    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void queryProjects(){

        showProgressBar();

        if(mProjects != null){
            if(mProjects.size() > 0){
                mProjects.clear();
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.collection_projects))
                .orderBy(getString(R.string.field_time_created), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Project project = document.toObject(Project.class);
                                mProjects.add(project);
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting projects: ", task.getException());
                            Toast.makeText(IssuesActivity.this, "error retrieving projects", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressBar();
                        updateFragments();
                    }
                });
    }

    private void updateFragments(){
        mProjectsFragment.updateProjectsList(mProjects);
        mIssuesFragment.updateProjectsList(mProjects);
    }

    private void setupViewPager(){
        mIssuesFragment = new IssuesFragment();
        mProjectsFragment = new ProjectsFragment();

        mIssuesPagerAdapter = new IssuesPagerAdapter(getSupportFragmentManager());
        mIssuesPagerAdapter.addFragment(mIssuesFragment);
        mIssuesPagerAdapter.addFragment(mProjectsFragment);

        mViewPager.setAdapter(mIssuesPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(ISSUES_FRAGMENT).setText(getString(R.string.fragment_issues));
        tabLayout.getTabAt(PROJECTS_FRAGMENT).setText(getString(R.string.fragment_projects));
    }


    @Override
    public void showProgressBar(){
        if(mProgressBar != null){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressBar(){
        if(mProgressBar != null){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void buildSnackbar(String message) {
        Snackbar.make(getCurrentFocus().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void getProjects() {
        queryProjects();
    }

    @Override
    public void deleteIssuesFromProject(ArrayList<Issue> issues, Project project) {
        mIssuesFragment.deleteIssuesFromProject(issues, project);
    }
}





























