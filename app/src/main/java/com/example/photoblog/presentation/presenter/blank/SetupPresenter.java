package com.example.photoblog.presentation.presenter.blank;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.R;
import com.example.photoblog.presentation.view.blank.SetupView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.photoblog.ui.activity.blank.SetupActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.photoblog.app.PhotoBlogApp.getAppContext;
import static com.example.photoblog.ui.activity.blank.SetupActivity.TAG;

@InjectViewState
public class SetupPresenter extends MvpPresenter<SetupView> {

    private Boolean isChanged = false;

    public void setProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getViewState().requestPermissions();
            } else {
                getViewState().bringImagePicker();
            }
        } else {
            getViewState().bringImagePicker();
        }
    }

    public void activityResult(int resultCode, @Nullable Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            try {
                getViewState().setImageAfterCrop(result.getUri());
            } catch (Exception e) {
                Log.e(TAG, "Error on: " + e);
            }
            isChanged = true;
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            getViewState().failedSetSettings(error.toString());
        }
    }

    public void setSettings(String user_name, Uri profileImageUri) {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(user_name) && profileImageUri != null) {
            getViewState().showProgress();
            if (isChanged) {
                StorageReference image_path = FirebaseStorage.getInstance().getReference().child("profile_images").child(user_id + ".jpg");
                image_path.putFile(profileImageUri).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return image_path.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeFirestore(task, user_name, user_id, profileImageUri);
                    } else {
                        String error = task.getException().getMessage();
                        getViewState().failedSetSettings(error);
                        getViewState().hideProgress();
                    }
                });
            } else {
                storeFirestore(null, user_name, user_id, profileImageUri);
            }
        }
    }

    public void storeFirestore(Task<Uri> task, String user_name, String user_id, Uri profileImageUri) {
        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult();
        } else {
            download_uri = profileImageUri;
        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());

        FirebaseFirestore.getInstance().collection("Users").document(user_id).set(userMap).addOnCompleteListener(fire_task -> {
            if (fire_task.isSuccessful()) {
                getViewState().successSetSettings();
            } else {
                getViewState().failedSetSettings(task.getException().getMessage());
            }
            getViewState().hideProgress();
        });

    }

    public void initUserData(String user_id) {
        FirebaseFirestore.getInstance().collection("Users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {

                    String name = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    getViewState().setProfileImageUri(Uri.parse(image));
                    getViewState().loadUserData(name, image);

                } else {
                    getViewState().failedSetSettings("Data not exists");
                }
                getViewState().hideProgress();
                getViewState().enableSetupBtn();
            }
        });
    }
}
