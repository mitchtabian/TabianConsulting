package courses.pluralsight.com.tabianconsulting.issues;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;


/**
 * Created by User on 4/16/2018.
 */

public class ProjectsFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ProjectsRecyclerViewAdapter.RecyclerViewClickListener
{

    private static final String TAG = "ProjectsFragment";

    //widgets
    private ImageView mAddIcon;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public Toolbar mToolbar;

    //vars
    private ProjectsRecyclerViewAdapter mProjectsRecyclerViewAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IIssues mIIssues;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        mAddIcon = view.findViewById(R.id.add_new);
        mSearchView = view.findViewById(R.id.action_search);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mToolbar = view.findViewById(R.id.toolbar);

        mAddIcon.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        initSearchView();
        initRecyclerView();

        return view;
    }

    private void getProjects(){
        mIIssues.getProjects();
    }

    public void updateProjectsList(ArrayList<Project> projects){

        if(mProjects != null){
            if(mProjects.size() > 0){
                mProjects.clear();
            }
        }

        if(projects != null){
            if(projects.size() > 0){
                mProjects.addAll(projects);
                mProjectsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initRecyclerView(){
        mProjectsRecyclerViewAdapter = new ProjectsRecyclerViewAdapter(mProjects, getActivity(), this);
        mRecyclerView.setAdapter(mProjectsRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initSearchView(){
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mProjectsRecyclerViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mProjectsRecyclerViewAdapter.getFilter().filter(query);
                return false;
            }
        });
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
    public void onRefresh() {
        getProjects();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete(){
        mSwipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
        intent.putExtra(getString(R.string.intent_project), mProjects.get(position));
        getContext().startActivity(intent);
    }
}
















