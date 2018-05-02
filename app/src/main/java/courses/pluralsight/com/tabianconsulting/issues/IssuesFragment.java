package courses.pluralsight.com.tabianconsulting.issues;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;


/**
 * Created by User on 4/16/2018.
 */

public class IssuesFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        IssuesRecyclerViewAdapter.RecyclerViewClickListener
{

    private static final String TAG = "IssuesFragment";

    //widgets
    private ImageView mAddIcon;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mProjectSpinner;
    public Toolbar mToolbar;


    //vars
    private IIssues mIIssues;
    private ArrayList<Issue> mIssues = new ArrayList<>();
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IssuesRecyclerViewAdapter mIssuesRecyclerViewAdapter;
    private Project mSelectedProject;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    public ActionMode mActionMode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issues, container, false);
        mAddIcon = view.findViewById(R.id.add_new);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mProjectSpinner = view.findViewById(R.id.project_spinner);
        mToolbar = view.findViewById(R.id.toolbar);

        mAddIcon.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getProjects();

        return view;
    }

    public void updateProjectsList(ArrayList<Project> projects){

        if(mProjects != null){
            if(mProjects.size() > 0){
                mProjects.clear();
            }
        }

        if(projects != null){
            mProjects.addAll(projects);
            initProjectsSpinner();
            initRecyclerView();
        }
    }

    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int[]  icons = {R.drawable.ic_task_blue, R.drawable.red_bug};
        mIssuesRecyclerViewAdapter = new IssuesRecyclerViewAdapter(getActivity(), mIssues, icons, this);
        mRecyclerView.setAdapter(mIssuesRecyclerViewAdapter);
    }

    private void initProjectsSpinner(){

        String[] projects = new String[mProjects.size()];
        for(int i = 0; i < mProjects.size(); i++){
            projects[i] = mProjects.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, projects);
        mProjectSpinner.setAdapter(adapter);

        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedProject = mProjects.get(i);
                getIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(mProjects.size() > 0){
            mSelectedProject = mProjects.get(0);
        }
    }

    private void getIssues(){
        if(mSelectedProject != null){

            mIIssues.showProgressBar();

            if(mIssues != null){
                if(mIssues.size() > 0){
                    mIssues.clear();
                }
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(getString(R.string.collection_projects))
                    .document(mSelectedProject.getProject_id())
                    .collection(getString(R.string.collection_issues))
                    .orderBy(getString(R.string.field_priority), Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    Issue issue = documentSnapshot.toObject(Issue.class);
                                    mIssues.add(issue);
                                }
                            }
                            else{
                                Toast.makeText(getActivity(), "error getting issues for that project", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: errors getting issues for project id: " + mSelectedProject.getProject_id());
                            }
                            mIIssues.hideProgressBar();
                            mIssuesRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void getProjects(){
        mIIssues.getProjects();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.add_new:{
                //go to NewIssueActivity
                Intent intent = new Intent(getActivity(), NewIssueActivity.class);
                startActivityForResult(intent, ResultCodes.SNACKBAR_RESULT_CODE);
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mIIssues = (IIssues) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
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
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getIssues();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        }
        else{
            Intent intent = new Intent(getActivity(), IssueDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_issue), mIssues.get(position));
            getActivity().startActivity(intent);
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

    private void deleteSelectedIssues(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final WriteBatch batch = db.batch();

        final ArrayList<Issue> deletedIssues = new ArrayList<>();
        for(int i = 0; i < mIssues.size(); i++){
            if(mIssuesRecyclerViewAdapter.isSelected(i)){

                DocumentReference ref = db
                        .collection(getString(R.string.collection_projects))
                        .document(mIssues.get(i).getProject_id())
                        .collection(getString(R.string.collection_issues))
                        .document(mIssues.get(i).getIssue_id());

                batch.delete(ref);

                deletedIssues.add(mIssues.get(i));
            }
        }
        mIssues.removeAll(deletedIssues);
        mIssuesRecyclerViewAdapter.notifyDataSetChanged();
        executeBatchCommit(batch);
    }

    public void deleteIssuesFromProject(ArrayList<Issue> issues, Project project){

        mIssues.removeAll(issues);
        mIssuesRecyclerViewAdapter.notifyDataSetChanged();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final WriteBatch batch = db.batch();

        for(int i = 0; i < issues.size(); i++){

            DocumentReference ref = db
                    .collection(getString(R.string.collection_projects))
                    .document(project.getProject_id())
                    .collection(getString(R.string.collection_issues))
                    .document(issues.get(i).getIssue_id());
            batch.delete(ref);

            Log.d(TAG, "deleteIssuesFromProject: queueing up issue for delete: " + issues.get(i).getIssue_id());
        }

        executeBatchCommit(batch);

        // delete the project
        db.collection(getString(R.string.collection_projects))
                .document(project.getProject_id())
                .delete();

        Log.d(TAG, "deleteIssuesFromProject: deleted project with id: " + project.getProject_id());
    }


    private void executeBatchCommit(WriteBatch batch) {
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: deleted the selected issues.");
                } else {
                    Log.d(TAG, "onComplete: could not delete the selected issues.");
                }
            }
        });
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

    private void toggleSelection(int position) {
        mIssuesRecyclerViewAdapter.toggleSelection(position);
        int count = mIssuesRecyclerViewAdapter.getSelectedItemCount();

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
                            .setMessage("Do you really want to delete these Issues?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Log.d(TAG, "menu_remove");
                                    mode.finish();
                                    deleteSelectedIssues();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mIssuesRecyclerViewAdapter.clearSelection();
            mActionMode = null;
            showToolbar();
        }
    }
}
















