package courses.pluralsight.com.tabianconsulting.issues;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.utility.FilePaths;
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
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    public ActionMode mActionMode;


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

    private void deleteSelectedProjects(){

        final ArrayList<Project> deletedProjects = new ArrayList<>();
        for(int i = 0; i < mProjects.size(); i++){
            if(mProjectsRecyclerViewAdapter.isSelected(i)){
                Log.d(TAG, "deleteProjects: queueing up project for delete: " + mProjects.get(i).getProject_id());
                deletedProjects.add(mProjects.get(i));
            }
        }

        mProjects.removeAll(deletedProjects);
        mProjectsRecyclerViewAdapter.notifyDataSetChanged();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for(int i = 0; i < deletedProjects.size(); i++){

            Log.d(TAG, "deleteProjects: deleting project with id: " + deletedProjects.get(i).getProject_id());

            final Project project = deletedProjects.get(i);

            db.collection(getString(R.string.collection_projects))
                    .document(project.getProject_id())
                    .collection(getString(R.string.collection_issues))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        ArrayList<Issue> issues = new ArrayList<>();

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            Log.d(TAG, "onComplete: adding issue to the list for deleting: " + documentSnapshot.getId());
                            Issue issue = documentSnapshot.toObject(Issue.class);
                            issues.add(issue);
                        }

                        // delete issues and attachments via IssuesFragment
                        mIIssues.deleteIssuesFromProject(issues, project);
                    }
                    else{
                        Log.d(TAG, "onComplete: error finding issues.");
                    }
                }
            });
        }
    }


    public void hideToolbar(){
        if(mToolbar != null){
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void showToolbar(){
        if(mToolbar != null){
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        }
        else{
            Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_project), mProjects.get(position));
            getContext().startActivity(intent);
        }

    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        mProjectsRecyclerViewAdapter.toggleSelection(position);
        int count = mProjectsRecyclerViewAdapter.getSelectedItemCount();

        if (count == 0) {
            showToolbar();
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            hideToolbar();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete")
                            .setMessage("Do you really want to delete these Projects?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Log.d(TAG, "menu_remove");
                                    mode.finish();
                                    deleteSelectedProjects();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "onDestroyActionMode: called.");
            mProjectsRecyclerViewAdapter.clearSelection();
            mActionMode = null;
            showToolbar();
        }
    }
}
















