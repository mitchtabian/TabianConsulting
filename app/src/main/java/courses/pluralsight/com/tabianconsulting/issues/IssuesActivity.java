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

/**
 * Created by User on 4/16/2018.
 */

public class IssuesActivity extends AppCompatActivity {

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


}





























