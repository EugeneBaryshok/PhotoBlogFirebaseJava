
package com.example.photoblog.presentation.presenter.blank;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.photoblog.R;
import com.example.photoblog.presentation.view.blank.LoginView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import com.google.firebase.auth.FirebaseAuth;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    public void signIn(String email, String password) {
        Integer emailError = null;
        Integer passwordError = null;

        if (TextUtils.isEmpty(email)) {
            emailError = R.string.error_field_required;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError = R.string.error_invalid_password;
        }

        if (emailError != null || passwordError != null) {
            getViewState().showFormError(emailError, passwordError);
            return;
        }

        getViewState().startSignIn();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                getViewState().finishSignIn();
                getViewState().showMain();
            }
            else
            {
                getViewState().finishSignIn();
                getViewState().failedSignIn(task.getException().getMessage());
            }
        });
    }
}
