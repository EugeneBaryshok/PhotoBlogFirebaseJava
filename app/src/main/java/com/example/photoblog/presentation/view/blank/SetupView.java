package com.example.photoblog.presentation.view.blank;

import android.net.Uri;

import com.arellomobile.mvp.MvpView;

public interface SetupView extends MvpView {

    void goToMain();

    void attempSetProfileImage();

    void bringImagePicker();

    void requestPermissions();

    void showProgress();

    void hideProgress();

    void successSetSettings();

    void failedSetSettings(String error);

    void attempSetSettings();

    void setImageAfterCrop(Uri uri);

    void setProfileImageUri(Uri uri);

    void loadUserData(String name, String image);

    void enableSetupBtn();

    void disableSetupBtn();

}
