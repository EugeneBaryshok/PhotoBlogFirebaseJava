package com.example.photoblog.presentation.presenter.blank;


import android.text.TextUtils;

import com.example.photoblog.R;
import com.example.photoblog.presentation.view.blank.RegisterView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;

@InjectViewState
public class RegisterPresenter extends MvpPresenter<RegisterView> {

    public void register(String email, String password, String confPassword)
    {
        Integer emailError = null;
        Integer passwordError = null;
        Integer passwordConfError = null;

        if (TextUtils.isEmpty(email)) {
            emailError = R.string.error_field_required;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError = R.string.error_invalid_password;
        }

        if (TextUtils.isEmpty(confPassword)) {
            passwordConfError = R.string.error_invalid_password;
        }

        if (emailError != null || passwordError != null || passwordConfError!=null) {
            getViewState().showFormError(emailError, passwordError,passwordConfError);
            return;
        }

        if (!password.equals(confPassword)) {
            getViewState().showPasswError();
            return;
        }

        getViewState().startRegister();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                getViewState().finishRegister();
                getViewState().goToMain();
            }
            else
            {
                getViewState().finishRegister();
                getViewState().failedRegister(task.getException().getMessage());
            }
        });

    }

}
