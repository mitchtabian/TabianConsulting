package courses.pluralsight.com.tabianconsulting.issues;

<<<<<<< HEAD
import android.animation.ValueAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
=======
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
>>>>>>> Module_7.5_End
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
<<<<<<< HEAD
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


=======
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

>>>>>>> Module_7.5_End
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
<<<<<<< HEAD
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
=======
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
>>>>>>> Module_7.5_End
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
<<<<<<< HEAD
import java.lang.reflect.Array;
=======
>>>>>>> Module_7.5_End
import java.util.ArrayList;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Attachment;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.utility.FilePaths;
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;
<<<<<<< HEAD
import courses.pluralsight.com.tabianconsulting.utility.SpinnerResource;
=======

>>>>>>> Module_7.5_End

/**
 * Created by User on 4/16/2018.
 */

public class IssuesFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
<<<<<<< HEAD
        IssuesRecyclerViewAdapter.RecyclerViewClickListener{
=======
        IssuesRecyclerViewAdapter.RecyclerViewClickListener
{
>>>>>>> Module_7.5_End

    private static final String TAG = "IssuesFragment";

    //widgets
    private ImageView mAddIcon;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mProjectSpinner;
    public Toolbar mToolbar;

<<<<<<< HEAD
    //vars
    private IIssues mIIssues;
    private ArrayList<Project> mProjects = new ArrayList<>();
    private ArrayList<Issue> mIssues = new ArrayList<>();
    private Project mSelectedProject;
    private IssuesRecyclerViewAdapter mIssuesRecyclerViewAdapter;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    public ActionMode mActionMode;

=======

    //vars
    private IIssues mIIssues;
    private ArrayList<Issue> mIssues = new ArrayList<>();
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IssuesRecyclerViewAdapter mIssuesRecyclerViewAdapter;
    private Project mSelectedProject;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    public ActionMode mActionMode;


>>>>>>> Module_7.5_End
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

<<<<<<< HEAD

=======
>>>>>>> Module_7.5_End
    public void updateProjectsList(ArrayList<Project> projects){

        if(mProjects != null){
            if(mProjects.size() > 0){
                mProjects.clear();
            }
        }

        if(projects != null){
<<<<<<< HEAD
//            if(projects.size() > 0){
//                mProjects.addAll(projects);
//                initProjectsSpinner();
//                initRecyclerView();
//            }
=======
>>>>>>> Module_7.5_End
            mProjects.addAll(projects);
            initProjectsSpinner();
            initRecyclerView();
        }
    }

<<<<<<< HEAD
=======
    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int[]  icons = {R.drawable.ic_task_blue, R.drawable.red_bug};
        mIssuesRecyclerViewAdapter = new IssuesRecyclerViewAdapter(getActivity(), mIssues, icons, this);
        mRecyclerView.setAdapter(mIssuesRecyclerViewAdapter);
    }
>>>>>>> Module_7.5_End

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

<<<<<<< HEAD

=======
>>>>>>> Module_7.5_End
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
<<<<<<< HEAD

=======
>>>>>>> Module_7.5_End
    }

    private void getProjects(){
        mIIssues.getProjects();
    }

<<<<<<< HEAD
    private void initRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int[]  icons = {R.drawable.ic_task_blue, R.drawable.red_bug};
        mIssuesRecyclerViewAdapter = new IssuesRecyclerViewAdapter(getActivity(), mIssues, icons, this);
        mRecyclerView.setAdapter(mIssuesRecyclerViewAdapter);
    }


=======
>>>>>>> Module_7.5_End
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
<<<<<<< HEAD
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
=======
>>>>>>> Module_7.5_End
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
<<<<<<< HEAD
=======
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
>>>>>>> Module_7.5_End
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        }
        else{
            Intent intent = new Intent(getActivity(), IssueDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_issue), mIssues.get(position));
            getActivity().startActivity(intent);
        }
<<<<<<< HEAD

=======
>>>>>>> Module_7.5_End
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

<<<<<<< HEAD
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

=======
>>>>>>> Module_7.5_End
    private void deleteSelectedIssues(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final WriteBatch batch = db.batch();

        final ArrayList<Issue> deletedIssues = new ArrayList<>();
        for(int i = 0; i < mIssues.size(); i++){
            if(mIssuesRecyclerViewAdapter.isSelected(i)){
<<<<<<< HEAD
=======

>>>>>>> Module_7.5_End
                DocumentReference ref = db
                        .collection(getString(R.string.collection_projects))
                        .document(mIssues.get(i).getProject_id())
                        .collection(getString(R.string.collection_issues))
                        .document(mIssues.get(i).getIssue_id());
<<<<<<< HEAD
                batch.delete(ref);

                Log.d(TAG, "deleteSelectedIssues: queueing up issue for delete: " + mIssues.get(i).getIssue_id());
=======

                batch.delete(ref);

>>>>>>> Module_7.5_End
                deletedIssues.add(mIssues.get(i));
            }
        }

        deleteAttachments(deletedIssues, batch, null);
    }

<<<<<<< HEAD

=======
>>>>>>> Module_7.5_End
    public void deleteAttachments(final ArrayList<Issue> deletedIssues, final WriteBatch batch, final Project project){

        Log.d(TAG, "deleteAttachments: deleting issues and attachments.");

        mIssues.removeAll(deletedIssues);
<<<<<<< HEAD
=======
        mIssuesRecyclerViewAdapter.notifyDataSetChanged();
>>>>>>> Module_7.5_End

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(deletedIssues.size() > 0){
            for(int i = 0; i < deletedIssues.size(); i++){

                Log.d(TAG, "deleteAttachments: deleting issue with id: " + deletedIssues.get(i).getIssue_id());

                final Issue issue = deletedIssues.get(i);

                final int index = i;

                db.collection(getString(R.string.collection_projects))
                        .document(issue.getProject_id())
                        .collection(getString(R.string.collection_issues))
                        .document(issue.getIssue_id())
                        .collection(getString(R.string.collection_attachments))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                Log.d(TAG, "onComplete: deleting attachment with id: " + documentSnapshot.getId());

                                Attachment attachment = documentSnapshot.toObject(Attachment.class);

                                // Get the url
                                final String url = attachment.getUrl();

                                // Get the attachment name
                                int startingIndex = url.indexOf(issue.getIssue_id()) + issue.getIssue_id().length() + 3;
                                int endingIndex = url.indexOf("?");
                                final String attachmentFileName = url.substring(startingIndex, endingIndex);
                                Log.d(TAG, "removeAttachments: attachment name: " + attachmentFileName);

                                deleteAttachmentDocument(issue, documentSnapshot.getId(), attachmentFileName);
                            }

                            if(index == deletedIssues.size() - 1){
                                if(batch == null){
                                    Log.d(TAG, "deleteAttachments: batch is NULL.");
                                    deleteIssuesFromProject(deletedIssues, project);
                                }
                                else{
                                    executeBatchCommit(batch);
                                }
                            }
                        }
                        else{
                            Log.d(TAG, "onComplete: error finding attachment.");
                        }
                    }
                });
            }
        }
        else{
<<<<<<< HEAD
            if(batch == null){
                Log.d(TAG, "deleteAttachments: batch is NULL.");
                deleteIssuesFromProject(deletedIssues, project);
            }
            else{
                executeBatchCommit(batch);
            }
=======
            Log.d(TAG, "deleteAttachments: batch is NULL.");
            deleteIssuesFromProject(deletedIssues, project);
>>>>>>> Module_7.5_End
        }
    }

    private void deleteAttachmentDocument(final Issue issue, final String attachmentId, final String attachmentFileName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.collection_projects))
                .document(issue.getProject_id())
                .collection(getString(R.string.collection_issues))
                .document(issue.getIssue_id())
                .collection(getString(R.string.collection_attachments))
                .document(attachmentId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: deleted attachment: " + attachmentId);
                    deleteAttachmentFromStorage(issue, attachmentFileName);
                }
                else{
                    Log.d(TAG, "onComplete: failed to delete attachment: " + attachmentId);
                }
            }
        });
    }

    private void deleteAttachmentFromStorage(final Issue issue, final String filename){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        FilePaths filePaths = new FilePaths();
        StorageReference filePathRef = storageRef.child(filePaths.FIREBASE_ISSUE_IMAGE_STORAGE
                + File.separator + issue.getIssue_id()
                + File.separator + filename);

        Log.d(TAG, "deleteAttachmentFromStorage: removing from storage: " + filePathRef);

        filePathRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: SUCCESSFULLY deleted file: " + filename);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onSuccess: FAILED to delete file: " + filename);
            }
        });
    }

<<<<<<< HEAD
    private void executeBatchCommit(WriteBatch batch){
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: deleted the selected issues.");
                    mIssuesRecyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "onComplete: could not delete the selected issues.");
                }
            }
        });
    }

    private void deleteIssuesFromProject(ArrayList<Issue> issues, Project project){
=======

    public void deleteIssuesFromProject(ArrayList<Issue> issues, Project project){

        mIssues.removeAll(issues);
        mIssuesRecyclerViewAdapter.notifyDataSetChanged();
>>>>>>> Module_7.5_End

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final WriteBatch batch = db.batch();

        for(int i = 0; i < issues.size(); i++){

            DocumentReference ref = db
                    .collection(getString(R.string.collection_projects))
<<<<<<< HEAD
                    .document(issues.get(i).getProject_id())
=======
                    .document(project.getProject_id())
>>>>>>> Module_7.5_End
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


<<<<<<< HEAD
=======
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

>>>>>>> Module_7.5_End
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
















