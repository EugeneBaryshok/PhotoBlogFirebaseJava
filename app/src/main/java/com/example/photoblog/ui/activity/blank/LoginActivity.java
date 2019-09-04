package com.example.photoblog.ui.activity.blank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.example.photoblog.presentation.view.blank.LoginView;
import com.example.photoblog.presentation.presenter.blank.LoginPresenter;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {
    public static final String TAG = "LoginActivity";
    private Unbinder unbinder;

    @InjectPresenter
    LoginPresenter mLoginPresenter;

    private FirebaseAuth mAuth;

    @BindView(R.id.login_email)
    EditText mEmailView;
    @BindView(R.id.login_pass)
    EditText mPasswordView;
    @BindView(R.id.login_reg_btn)
    Button mSignInButton;
    @BindView(R.id.need_acc_btn)
    Button mNeedAccButton;
    @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showMain();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mSignInButton.setOnClickListener(view -> attemptLogin());
        mNeedAccButton.setOnClickListener(view -> showRegister());
    }

    @Override
    public void showMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void failedSignIn(String message) {
        Toast.makeText(this, "Error on: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startSignIn() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishSignIn() {
        toggleProgressVisibility(false);
    }

    private void attemptLogin() {
        mLoginPresenter.signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    @Override
    public void showFormError(Integer emailError, Integer passwordError) {
        mEmailView.setError(emailError == null ? null : getString(emailError));
        mPasswordView.setError(passwordError == null ? null : getString(passwordError));
    }

    private void toggleProgressVisibility(final boolean show) {
        mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }


}
