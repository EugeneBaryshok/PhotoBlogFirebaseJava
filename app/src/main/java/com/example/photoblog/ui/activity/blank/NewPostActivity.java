package com.example.photoblog.ui.activity.blank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.photoblog.presentation.view.blank.NewPostView;
import com.example.photoblog.presentation.presenter.blank.NewPostPresenter;

import com.arellomobile.mvp.MvpAppCompatActivity;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.zelory.compressor.Compressor;

public class NewPostActivity extends MvpAppCompatActivity implements NewPostView {
    public static final String TAG = "NewPostActivity";
    private Unbinder unbinder;
    private Uri postImageUri = null;
    @InjectPresenter
    NewPostPresenter mNewPostPresenter;

    @BindView(R.id.new_post_toolbar)
    Toolbar mPostTollbar;
    @BindView(R.id.post_btn)
    Button mAddPostBtn;
    @BindView(R.id.new_post_desc)
    EditText mNewPostDesc;
    @BindView(R.id.new_post_image)
    ImageView mNewPostImage;
    @BindView(R.id.new_post_progressBar)
    ProgressBar mNewPostProgress;


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, NewPostActivity.class);

        return intent;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        unbinder  = ButterKnife.bind(this);

        mNewPostPresenter.initFirebase();

        setSupportActionBar(mPostTollbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNewPostImage.setOnClickListener(v -> {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512,512)
                    .setAspectRatio(1, 1)
                    .start(NewPostActivity.this);
        });

        mAddPostBtn.setOnClickListener(v->{
            String desc = mNewPostDesc.getText().toString();
            mNewPostPresenter.addPost(desc, postImageUri);
        });
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, "Error on: "+error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setImageUri(Uri uri) {
        postImageUri = uri;
        mNewPostImage.setImageURI(postImageUri);
    }

    @Override
    public void showProgress() {
        mNewPostProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mNewPostProgress.setVisibility(View.GONE);
    }

    @Override
    public void postLoadedSuccess() {
        goToMain();
        Toast.makeText(this, "Post added", Toast.LENGTH_LONG).show();
    }

    @Override
    public void postLoadedFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            mNewPostPresenter.activityResult(resultCode, data);
        }
    }

    public void goToMain()
    {
        Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
        startActivity(intent);
        fileList();
    }
}
