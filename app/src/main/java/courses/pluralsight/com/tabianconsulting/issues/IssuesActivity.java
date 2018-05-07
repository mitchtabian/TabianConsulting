package courses.pluralsight.com.tabianconsulting.issues;

<<<<<<< HEAD
import android.app.Application;
import android.content.Intent;
=======
>>>>>>> Module_7.5_End
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
=======
import android.util.Log;
import android.view.View;
>>>>>>> Module_7.5_End
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
<<<<<<< HEAD
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
=======
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
>>>>>>> Module_7.5_End


import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;
<<<<<<< HEAD
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;
=======
>>>>>>> Module_7.5_End

/**
 * Created by User on 4/16/2018.
 */

<<<<<<< HEAD
public class IssuesActivity extends AppCompatActivity implements IIssues{
=======
public class IssuesActivity extends AppCompatActivity implements IIssues {
>>>>>>> Module_7.5_End

    private static final String TAG = "IssuesActivity";

    private static final int ISSUES_FRAGMENT = 0;
    private static final int PROJECTS_FRAGMENT = 1;

    //widgets
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;

    //vars
<<<<<<< HEAD
    private IssuesPagerAdapter mIssuesPagerAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IssuesFragment mIssuesFragment;
    private ProjectsFragment mProjectsFragment;
=======
    private IssuesFragment mIssuesFragment;
    private ProjectsFragment mProjectsFragment;
    private IssuesPagerAdapter mIssuesPagerAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();
>>>>>>> Module_7.5_End

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        mViewPager = findViewById(R.id.main_container);
        mProgressBar = findViewById(R.id.progress_bar);

        setupActionBar();
        setupViewPager();
    }

<<<<<<< HEAD
=======

    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

>>>>>>> Module_7.5_End
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

<<<<<<< HEAD
    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

=======
>>>>>>> Module_7.5_End
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

<<<<<<< HEAD
=======

>>>>>>> Module_7.5_End
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

<<<<<<< HEAD

    @Override
    public void deleteIssuesAndAttachments(ArrayList<Issue> issues, Project project) {
        mIssuesFragment.deleteAttachments(issues, null, project);
=======
    @Override
    public void deleteIssuesFromProject(ArrayList<Issue> issues, Project project) {
        mIssuesFragment.deleteAttachments(issues, null , project);
>>>>>>> Module_7.5_End
    }
}





























