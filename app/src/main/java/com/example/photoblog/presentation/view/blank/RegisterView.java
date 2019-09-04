package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;

public interface RegisterView extends MvpView {

    void startRegister();

    void finishRegister();

    void failedRegister(String message);

    void showFormError(Integer emailError, Integer passwordError, Integer confPasswordError);

    void showPasswError();

    void goToLogin();

    void goToSettings();

    void goToMain();
}
