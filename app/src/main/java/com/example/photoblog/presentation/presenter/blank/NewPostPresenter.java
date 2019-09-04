package com.example.photoblog.presentation.presenter.blank;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.photoblog.presentation.view.blank.NewPostView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.example.photoblog.app.PhotoBlogApp.getAppContext;
import static com.example.photoblog.ui.activity.blank.SetupActivity.TAG;

@InjectViewState
public class NewPostPresenter extends MvpPresenter<NewPostView> {

    private String user_id;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;

    public void activityResult(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            try {
                getViewState().setImageUri(result.getUri());
            } catch (Exception e) {
                Log.e(TAG, "Error on: " + e);
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            getViewState().showError(error.toString());
        }
    }

    public void initFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addPost(String desc, Uri uri) {

        if (!TextUtils.isEmpty(desc) && uri != null) {
            getViewState().showProgress();
            final String randomName = UUID.randomUUID().toString();

            File newImageFile = new File(uri.getPath());
            try {

                compressedImageFile = new Compressor(getAppContext())
                        .setMaxHeight(720)
                        .setMaxWidth(720)
                        .setQuality(50)
                        .compressToBitmap(newImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageData = baos.toByteArray();

            StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
            filePath.putBytes(imageData).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    File newThumbFile = new File(uri.getPath());
                    try {

                        compressedImageFile = new Compressor(getAppContext())
                                .setMaxHeight(100)
                                .setMaxWidth(100)
                                .setQuality(1)
                                .compressToBitmap(newThumbFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                    byte[] thumbData = baos2.toByteArray();

                    StorageReference filePath2 = storageReference.child("post_images/thumbs")
                            .child(randomName + ".jpg");
                    filePath2.putBytes(thumbData).continueWithTask(task2 -> {
                        if (!task2.isSuccessful()) {
                            throw task2.getException();
                        }
//                        downloadthumbUri = filePath2.getDownloadUrl().toString();
                        return filePath2.getDownloadUrl();
                    }).addOnCompleteListener(task2 -> {
                        if(task2.isSuccessful())
                        {
                            storeFirestore(task.getResult().toString(), task2.getResult().toString(), desc);
                        }else
                        {
                            getViewState().showError(task2.getException().getMessage());
                        }

                    });


                } else {
                    String error = task.getException().getMessage();
                    getViewState().showError(error);
                    getViewState().hideProgress();
                }
            });
        }
    }
    public void storeFirestore(String downloadthumbUri, String downloadUri, String desc)
    {

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("image_url",downloadUri);
        postMap.put("image_thumb",downloadthumbUri);
        postMap.put("desc",desc);
        postMap.put("user_id",user_id);
        postMap.put("timestamp",FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("Posts").add(postMap).addOnCompleteListener(task1 ->
        {
            if(task1.isSuccessful())
            {
                getViewState().postLoadedSuccess();
            }
            else
            {
                String error = task1.getException().getMessage();
                getViewState().postLoadedFailed(error);
            }
            getViewState().hideProgress();
        });
    }
}
