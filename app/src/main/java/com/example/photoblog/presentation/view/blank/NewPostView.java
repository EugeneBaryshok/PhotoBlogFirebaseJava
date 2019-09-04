package com.example.photoblog.presentation.view.blank;

import android.graphics.Bitmap;
import android.net.Uri;

import com.arellomobile.mvp.MvpView;

public interface NewPostView extends MvpView {

    void showError(String error);

    void setImageUri(Uri uri);

    void showProgress();

    void hideProgress();

    void postLoadedSuccess();

    void postLoadedFailed(String error);

//    Bitmap compressImage(Uri uri);
}
