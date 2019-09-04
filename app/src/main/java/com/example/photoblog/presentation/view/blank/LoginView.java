package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;

public interface LoginView extends MvpView {
    void startSignIn();

    void finishSignIn();

    void failedSignIn(String message);

    void showFormError(Integer emailError, Integer passwordError);

    void showMain();

    void showRegister();
}
