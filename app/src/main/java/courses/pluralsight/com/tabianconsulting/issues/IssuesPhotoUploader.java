package courses.pluralsight.com.tabianconsulting.issues;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.models.Attachment;
import courses.pluralsight.com.tabianconsulting.utility.FilePaths;


/**
 * Created by User on 4/16/2018.
 */

public class IssuesPhotoUploader {

    public interface AttachmentUploadCallback{
        void updateImageUrl(String downloadUrl, String localImagePath);
    }

    private static final String TAG = "IssuesPhotoUploader";
    private static final double MB_THRESHHOLD = 5.0;
    private static final double MB = 1000000.0;

    private Context mContext;
    private String mProjectId, mIssueId;
    private byte[] mBytes;
    private double progress;
    private boolean mIsUploadingAttachments = false;
    private BackgroundConversion mConvert;
    private Uri mUploadUri;
    private AttachmentUploadCallback mAttachmentUploadCallback;


    public IssuesPhotoUploader(Context context, String projectId, String issueId, AttachmentUploadCallback callback) {
        mContext = context;
        mProjectId = projectId;
        mIssueId = issueId;
        mAttachmentUploadCallback = callback;
    }


    public void uploadAttachment(Uri imageUri){
        mUploadUri = imageUri;
        mIsUploadingAttachments = true;
        uploadNewPhoto(imageUri);
    }


    /**
     * Uploads a new profile photo to Firebase Storage using a @param ***imageUri***
     * @param imageUri
     */
    public void uploadNewPhoto(Uri imageUri){
        Log.d(TAG, "uploadNewPhoto: uploading new image.");

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        if(mConvert != null){
            mConvert.cancel(true);
        }
        mConvert = new BackgroundConversion();
        mConvert.execute(imageUri);
    }


    public class BackgroundConversion extends AsyncTask<Uri, Integer, byte[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "doInBackground: started.");

            InputStream iStream = null;
            try {
                iStream = mContext.getContentResolver().openInputStream(params[0]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            try {
                while ((len = iStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return byteBuffer.toByteArray();
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mBytes = bytes;
            executeUploadTask();
        }
    }

    private void executeUploadTask(){
        FilePaths filePaths = new FilePaths();
        //specify where the photo will be stored

        String uploadPath = "";
        String format = "";
        if(!mIsUploadingAttachments){
            uploadPath = filePaths.FIREBASE_PROJECT_IMAGE_STORAGE + "/" + mProjectId
                            + "/project_avatar";
        }
        else{
            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
            format = s.format(new Date());
            uploadPath = filePaths.FIREBASE_ISSUE_IMAGE_STORAGE + "/" + mIssueId
                            + "/image_" + format;
        }
        final String imageName = "image_" + format;

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(uploadPath);

        if(mBytes.length/MB < MB_THRESHHOLD) {

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setContentLanguage("en")
                    .build();
            //if the image size is valid then we can submit to database
            UploadTask uploadTask = storageReference.putBytes(mBytes, metadata);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri firebaseURL = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: firebase download url : " + firebaseURL.toString());

                    // insert the download url into cloud firestore
                    if(!mIsUploadingAttachments){
                        updateProjectAvatar(firebaseURL.toString());
                    }
                    else{
                        setNewIssueAttachment(firebaseURL.toString(), imageName);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(mContext, "error uploading image", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(currentProgress > (progress + 15)){
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                    }

                }
            })
            ;
        }else{
            Toast.makeText(mContext, "Image is too Large", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNewIssueAttachment(final String downloadUrl, String filename){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newAttachmentRef =
        db.collection(mContext.getString(R.string.collection_projects))
                .document(mProjectId)
                .collection(mContext.getString(R.string.collection_issues))
                .document(mIssueId)
                .collection(mContext.getString(R.string.collection_attachments))
                .document();

        Attachment attachment = new Attachment();
        attachment.setName(filename);
        attachment.setUrl(downloadUrl);
        attachment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        newAttachmentRef.set(attachment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onComplete: new attachment attached to collection.");
                mAttachmentUploadCallback.updateImageUrl(downloadUrl, mUploadUri.toString());
            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onComplete: attachment upload failed.");
                    }
                });
    }

    private void updateProjectAvatar(String downloadUrl){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(mContext.getString(R.string.field_avatar), downloadUrl);

        db.collection(mContext.getString(R.string.collection_projects))
                .document(mProjectId)
                .update(
                        updates
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: avatar is updated.");
                }
                else{
                    Log.d(TAG, "onComplete: avatar updated failed.");
                }
            }
        });
    }

}

















