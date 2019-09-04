package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView {

    void sendToLogin();
    void goToSettings();
    void goToNewPost();
    void showError(String error);

}
