package com.example.photoblog.ui.activity.blank;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.presentation.view.blank.SetupView;
import com.example.photoblog.presentation.presenter.blank.SetupPresenter;

import com.arellomobile.mvp.MvpAppCompatActivity;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends MvpAppCompatActivity implements SetupView {
    public static final String TAG = "SetupActivity";
    @InjectPresenter
    SetupPresenter mSetupPresenter;
    @BindView(R.id.setup_toolbar)
    Toolbar setupToolbar;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;

    @BindView(R.id.setup_btn)
    Button setupBtn;
    @BindView(R.id.setup_name)
    EditText setupName;

    @BindView(R.id.setup_progress)
    ProgressBar setupProgress;

//    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
//    private FirebaseFirestore firebaseFirestore;
    private String user_id;


    private Uri profileImageUri;
    private Unbinder unbinder;


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, SetupActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(setupToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        setupProgress.setVisibility(View.VISIBLE);
        disableSetupBtn();

//        storageReference = FirebaseStorage.getInstance().getReference();
//        firebaseFirestore = FirebaseFirestore.getInstance();

        mSetupPresenter.initUserData(user_id);


        setupBtn.setOnClickListener(v -> {

            attempSetSettings();
        });

        profileImage.setOnClickListener(v -> {
            attempSetProfileImage();
        });

    }

    @Override
    public void enableSetupBtn() {
        setupBtn.setEnabled(true);
    }
    @Override
    public void disableSetupBtn() {
        setupBtn.setEnabled(false);
    }

    @Override
    public void setProfileImageUri(Uri uri) {
        profileImageUri = uri;
    }

    @Override
    public void loadUserData(String name, String image) {
        setupName.setText(name);

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.defaultprofile);

        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
    }

    @Override
    public void attempSetSettings() {
        String user_name = setupName.getText().toString();
        mSetupPresenter.setSettings(user_name, profileImageUri);
    }

    @Override
    public void setImageAfterCrop(Uri path) {
        profileImageUri = path;
        profileImage.setImageURI(path);
    }

    @Override
    public void attempSetProfileImage() {
        mSetupPresenter.setProfileImage();
    }

    @Override
    public void showProgress() {
        setupProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        setupProgress.setVisibility(View.GONE);
    }

    @Override
    public void successSetSettings() {
        Toast.makeText(SetupActivity.this, "The user settings are updated", Toast.LENGTH_LONG).show();
        goToMain();
    }

    @Override
    public void failedSetSettings(String error) {
        Toast.makeText(SetupActivity.this, "(FireStore error): " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goToMain() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    @Override
    public void requestPermissions() {
        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            mSetupPresenter.activityResult(resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
