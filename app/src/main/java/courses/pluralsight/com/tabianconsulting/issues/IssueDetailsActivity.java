package courses.pluralsight.com.tabianconsulting.issues;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import courses.pluralsight.com.tabianconsulting.ChangePhotoDialog;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Attachment;
import courses.pluralsight.com.tabianconsulting.models.Issue;
import courses.pluralsight.com.tabianconsulting.models.Project;
import courses.pluralsight.com.tabianconsulting.models.User;
import courses.pluralsight.com.tabianconsulting.utility.FilePaths;
import courses.pluralsight.com.tabianconsulting.utility.ResultCodes;
import courses.pluralsight.com.tabianconsulting.utility.SpinnerResource;


/**
 * Created by User on 4/23/2018.
 */

public class IssueDetailsActivity extends AppCompatActivity implements
        View.OnClickListener,
        IIssueDetail,
        ChangePhotoDialog.OnPhotoReceivedListener,
        IssuesPhotoUploader.AttachmentUploadCallback,
        AttachmentRecyclerViewAdapter.IsAttachmentsSelected
{

    private static final String TAG = "IssueDetailsActivity";
    private static final int REQUEST_CODE = 1234;
    private static final int RECYCLERVIEW_HORIZONTAL_SPACING = 10;

    //widgets
    private Spinner mIssueTypeSpinner, mPrioritySpinner, mStatusSpinner, mAssigneeSpinner;
    private TextView mAssignToProject;
    private EditText mSummary, mDescription;
    private LinearLayout mAddAttachment, mRemoveAttachments;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mSaveChanges;

    //vars
    private ArrayList<Project> mProjects = new ArrayList<>();
    private Issue mIssue;
    private ArrayList<User> mUsers = new ArrayList<>();
    private boolean mStoragePermissions;
    private AttachmentRecyclerViewAdapter mAttachmentRecyclerViewAdapter;
    public ArrayList<String> mAttachments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);
        mIssueTypeSpinner = findViewById(R.id.issue_type_spinner);
        mPrioritySpinner = findViewById(R.id.priority_spinner);
        mAssignToProject = findViewById(R.id.assign_to_project);
        mAddAttachment = findViewById(R.id.add_attachment);
        mRemoveAttachments = findViewById(R.id.remove_attachments);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSummary = findViewById(R.id.issue_summary);
        mDescription = findViewById(R.id.issue_description);
        mProgressBar = findViewById(R.id.progress_bar);
        mStatusSpinner = findViewById(R.id.status_spinner);
        mAssigneeSpinner = findViewById(R.id.assignee_spinner);
        mSaveChanges = findViewById(R.id.btn_save);


        mAddAttachment.setOnClickListener(this);
        mSaveChanges.setOnClickListener(this);
        mRemoveAttachments.setOnClickListener(this);

        if(getIssue()){
            setupActionBar();
            initIssueTypeSpinner();
            initPrioritySpinner();
            initStatusSpinner();
            getEmployeeList();
            setIssueDetails();
            initRecyclerView();
            verifyStoragePermissions();
        }
        else{
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean getIssue(){
        if(getIntent().hasExtra(getString(R.string.intent_issue))){
            mIssue = getIntent().getParcelableExtra(getString(R.string.intent_issue));
            getAttachments();
            return true;
        }
        return false;
    }

    private void getAttachments(){
        // get the document reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference newIssueRef = db
                .collection(getString(R.string.collection_projects))
                .document(mIssue.getProject_id())
                .collection(getString(R.string.collection_issues))
                .document(mIssue.getIssue_id())
                .collection(getString(R.string.collection_attachments));

        newIssueRef.orderBy(getString(R.string.field_timestamp), com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        mAttachments.add(documentSnapshot.toObject(Attachment.class).getUrl());
                    }
                    mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "onComplete: couldn't get attachments.");
                }
            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        HorizontalSpacingItemDecorator itemDecorator = new HorizontalSpacingItemDecorator(RECYCLERVIEW_HORIZONTAL_SPACING);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAttachmentRecyclerViewAdapter = new AttachmentRecyclerViewAdapter(this, mAttachments, this);
        mRecyclerView.setAdapter(mAttachmentRecyclerViewAdapter);
    }

    private void saveChanges(){

        hideSoftKeyboard();

        if(mSummary.getText().toString().equals("")){
            mSummary.setError(getString(R.string.required));
        }
        else{
            showProgressBar();

            // get the document reference
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference newIssueRef = db
                    .collection(getString(R.string.collection_projects))
                    .document(mIssue.getProject_id())
                    .collection(getString(R.string.collection_issues))
                    .document(mIssue.getIssue_id());

            // Create the issue and add send to database
            Issue issue = new Issue();
            issue.setAssignee(((SpinnerAdapter)mAssigneeSpinner.getAdapter()).getSelectedText());
            issue.setDescription(mDescription.getText().toString());
            issue.setIssue_id(mIssue.getIssue_id());
            issue.setIssue_type(((SpinnerAdapter)mIssueTypeSpinner.getAdapter()).getSelectedText());
            issue.setPriority(Issue.getPriorityInteger(((SpinnerAdapter)mPrioritySpinner.getAdapter()).getSelectedText()));
            issue.setReporter(mIssue.getReporter());
            issue.setStatus((String)mStatusSpinner.getSelectedItem());
            issue.setSummary(mSummary.getText().toString());
            issue.setProject_id(mIssue.getProject_id());
            issue.setTime_reported(mIssue.getTime_reported());

            newIssueRef.set(issue).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.intent_snackbar_message), getString(R.string.issue_edit_success));
                        setResult(ResultCodes.SNACKBAR_RESULT_CODE, intent);
                        finish();
                    }
                    else{
                        Snackbar.make(getCurrentFocus().getRootView(), getString(R.string.issue_edit_fail), Snackbar.LENGTH_LONG).show();
                    }
                    hideProgressBar();
                }
            });
        }

    }


    private void setIssueDetails(){
        mSummary.setText(mIssue.getSummary());
        mDescription.setText(mIssue.getDescription());
        getProjectName();
    }

    private void getProjectName(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.collection_projects))
                .document(mIssue.getProject_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String projectName = (String) documentSnapshot.get(getString(R.string.field_name));
                            mAssignToProject.setText(projectName);
                        }
                        else{
                            Toast.makeText(IssueDetailsActivity.this, "error getting project name", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: error getting project name.");
                        }
                    }
                });
    }

    private void setAssigneeSpinner(){
        if(!mIssue.getAssignee().equals("")){
            int position = ((SpinnerAdapter)mAssigneeSpinner.getAdapter())
                    .getPosition(((SpinnerAdapter)mAssigneeSpinner.getAdapter()).getSelectedText());
            mAssigneeSpinner.setSelection(position);
        }
        else{
            mAssigneeSpinner.setSelection(0);
        }
    }

    private void setStatusSpinner(){
        if(mIssue.getStatus().equals(Issue.IN_PROGRESS)){
            mStatusSpinner.setSelection(0);
        }
        else if(mIssue.getStatus().equals(Issue.DONE)){
            mStatusSpinner.setSelection(1);
        }
        else if(mIssue.getStatus().equals(Issue.IDLE)){
            mStatusSpinner.setSelection(2);
        }
        else{
            mStatusSpinner.setSelection(0);
        }
    }

    private void setIssueTypeSpinner(){
        if(mIssue.getIssue_type().equals(Issue.TASK)){
            mIssueTypeSpinner.setSelection(0);
        }
        else if(mIssue.getIssue_type().equals(Issue.BUG)){
            mIssueTypeSpinner.setSelection(1);
        }
    }

    private void setPrioritySpinner(){
        if(mIssue.getPriorityString().equals(Issue.HIGH)){
            mPrioritySpinner.setSelection(0);
        }
        else if(mIssue.getPriorityString().equals(Issue.MEDIUM)){
            mPrioritySpinner.setSelection(1);
        }
        else if(mIssue.getPriorityString().equals(Issue.LOW)){
            mPrioritySpinner.setSelection(2);
        }
        else{
            mPrioritySpinner.setSelection(0);
        }
    }


    private void setupActionBar(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    /**
     * Get a list of all employees
     * @throws NullPointerException
     */
    private void getEmployeeList() throws NullPointerException{
        Log.d(TAG, "getEmployeeList: getting a list of all employees");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                initAssigneeSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initAssigneeSpinner(){
        String[] usernames = new String[mUsers.size() + 1];
        String[] userImages = new String[mUsers.size() + 1];
        for(int i = 0; i < mUsers.size(); i++){
            usernames[i] = mUsers.get(i).getName();
            userImages[i] = mUsers.get(i).getProfile_image();
        }
        userImages[mUsers.size()] = "";
        usernames[mUsers.size()] = "none";

        final SpinnerAdapter adapter = new SpinnerAdapter(this, usernames, userImages);
        final String[] names = usernames;
        mAssigneeSpinner.setAdapter(adapter);
        adapter.setSelectedText(mIssue.getAssignee());

        mAssigneeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedText(names[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setAssigneeSpinner();
    }

    private void initStatusSpinner(){
        final String[] issueStatus = SpinnerResource.issue_status_spinner;
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, issueStatus);
        mStatusSpinner.setAdapter(adapter);

        setStatusSpinner();
    }

    private void initIssueTypeSpinner(){
        final String[] issueTypes = SpinnerResource.issue_type_spinner;
        int[] issueImages = SpinnerResource.issue_type_images_spinner;

        final SpinnerAdapter adapter = new SpinnerAdapter(this, issueTypes, issueImages);
        mIssueTypeSpinner.setAdapter(adapter);
        adapter.setSelectedText(mIssue.getIssue_type());

        mIssueTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedText(issueTypes[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setIssueTypeSpinner();
    }

    private void initPrioritySpinner(){
        final String[] priorityLevels = SpinnerResource.issue_priorities_spinner;
        int[] priorityImages = SpinnerResource.issue_priority_images_spinner;

        final SpinnerAdapter adapter = new SpinnerAdapter(this, priorityLevels, priorityImages);
        mPrioritySpinner.setAdapter(adapter);
        adapter.setSelectedText(mIssue.getPriorityString());

        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedText(priorityLevels[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setPrioritySpinner();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.add_attachment:{
                if(mStoragePermissions){
                    ChangePhotoDialog dialog = new ChangePhotoDialog();
                    dialog.show(getSupportFragmentManager(), getString(R.string.dialog_change_photo));
                }else{
                    verifyStoragePermissions();
                }
                break;
            }

            case R.id.remove_attachments:{
                removeAttachments();
                break;
            }

            case R.id.btn_save:{
                saveChanges();
                break;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }


    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        super.onBackPressed();
        if(mProgressBar.getVisibility() == View.VISIBLE){
            hideProgressBar();
        }
        showStatusBar();
    }


    private void hideStatusBar(){
        // Hide Status Bar
        View decorView = getWindow().getDecorView();
        // Hide Status Bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar(){
        View decorView = getWindow().getDecorView();
        // Show Status Bar.
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void buildSnackbar(String message) {
        Snackbar.make(getCurrentFocus().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Generalized method for asking permission. Can pass any array of permissions
     */
    public void verifyStoragePermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2] ) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissions = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }

    @Override
    public void getImagePath(Uri imagePath) {
        if( !imagePath.toString().equals("")){

            // Start Image Upload Process
            Log.d(TAG, "getImagePath: path: " + imagePath);
            IssuesPhotoUploader uploader = new IssuesPhotoUploader(this, mIssue.getProject_id(), mIssue.getIssue_id(), this);
            uploader.uploadAttachment(imagePath);
            mAttachments.add(imagePath.toString());
            mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void updateImageUrl(String downloadUrl, String localImagePath) {

        // Update the RecyclerView with attachments
        mAttachments.remove(localImagePath);
        mAttachments.add(downloadUrl);
        mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void inflateFullScreenImageFragment(final Object imageResource) {

        hideStatusBar();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.setImageResource(imageResource);

        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.fullscreen_container, fragment, getString(R.string.fragment_full_screen));
        transaction.addToBackStack(getString(R.string.fragment_full_screen));
        transaction.commit();
    }

    /**
     * sets the trash-can and "add attachment" visibilities
     * @param isSelected
     */
    @Override
    public void isSelected(boolean isSelected) {
        if(isSelected){
            removeAttachmentsMode();
        }
        else{
            addAttachmentMode();
        }
    }

    private void addAttachmentMode(){
        mAddAttachment.setVisibility(View.VISIBLE);
        mRemoveAttachments.setVisibility(View.GONE);
    }

    private void removeAttachmentsMode(){
        mAddAttachment.setVisibility(View.GONE);
        mRemoveAttachments.setVisibility(View.VISIBLE);
    }

    private void removeAttachments(){
        // get the attachments that are selected
        List<Integer> selectedAttachments = mAttachmentRecyclerViewAdapter.getSelectedItems();

        // iterate through and delete
        for(int i : selectedAttachments){

            if(!mAttachments.get(i).contains("tabianconsulting")){
                buildSnackbar("One of the images you selected is still uploading");
            }
            else{

                // Get the url
                final String url = mAttachments.get(i);

                // Get the attachment name
                int startingIndex = url.indexOf(mIssue.getIssue_id()) + mIssue.getIssue_id().length() + 3;
                int endingIndex = url.indexOf("?");
                final String attachmentFileName = url.substring(startingIndex, endingIndex);
                Log.d(TAG, "removeAttachments: attachment name: " + attachmentFileName);

                // Query Firestore for the attachment with the name above
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(getString(R.string.collection_projects))
                        .document(mIssue.getProject_id())
                        .collection(getString(R.string.collection_issues))
                        .document(mIssue.getIssue_id())
                        .collection(getString(R.string.collection_attachments))
                        .whereEqualTo(getString(R.string.field_name), attachmentFileName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        String id = documentSnapshot.getId();
                                        Log.d(TAG, "onComplete: deleting attachment with id: " + id);
                                        deleteAttachmentDocument(url, id, attachmentFileName);
                                    }
                                }
                                else{
                                    Log.d(TAG, "onComplete: error finding attachment.");
                                }
                            }
                        });

            }
        }
    }

    private void deleteAttachmentDocument(
            final String url, final String attachmentId, final String attachmentFileName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.collection_projects))
                .document(mIssue.getProject_id())
                .collection(getString(R.string.collection_issues))
                .document(mIssue.getIssue_id())
                .collection(getString(R.string.collection_attachments))
                .document(attachmentId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: deleted attachment: " + attachmentId);
                    deleteAttachmentFromStorage(attachmentFileName);
                    mAttachments.remove(url);
                    mAttachmentRecyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "onComplete: failed to delete attachment: " + attachmentId);
                }
            }
        });
    }

    private void deleteAttachmentFromStorage(final String filename){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        FilePaths filePaths = new FilePaths();
        StorageReference filePathRef = storageRef.child(filePaths.FIREBASE_ISSUE_IMAGE_STORAGE
                + File.separator + mIssue.getIssue_id()
                + File.separator + filename);

        Log.d(TAG, "deleteAttachmentFromStorage: removing from storage: " + filePathRef);

        filePathRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: SUCCESSFULLY deleted file: " + filename);
                mAttachmentRecyclerViewAdapter.clearSelection();
                isSelected(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onSuccess: FAILED to delete file: " + filename);
            }
        });
    }

}

















