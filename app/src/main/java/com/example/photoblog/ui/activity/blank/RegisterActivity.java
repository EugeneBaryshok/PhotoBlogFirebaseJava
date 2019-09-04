package com.example.photoblog.ui.activity.blank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.photoblog.presentation.view.blank.RegisterView;
import com.example.photoblog.presentation.presenter.blank.RegisterPresenter;

import com.arellomobile.mvp.MvpAppCompatActivity;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RegisterActivity extends MvpAppCompatActivity implements RegisterView {
    public static final String TAG = "RegisterActivity";
    @InjectPresenter
    RegisterPresenter mRegisterPresenter;

    private FirebaseAuth mAuth;
    private Unbinder unbinder;

    @BindView(R.id.register_email)
    EditText mEmailView;
    @BindView(R.id.register_pass)
    EditText mPasswordView;
    @BindView(R.id.register_conf_pass)
    EditText mConfPasswordView;
    @BindView(R.id.register_reg_btn)
    Button mRegisterButton;
    @BindView(R.id.have_acc_btn)
    Button mHaveAccButton;
    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);

        return intent;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void attemptRegister() {
        mRegisterPresenter.register(mEmailView.getText().toString(), mPasswordView.getText().toString(), mConfPasswordView.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        unbinder= ButterKnife.bind(this);

        mRegisterButton.setOnClickListener(v -> attemptRegister());
        mHaveAccButton.setOnClickListener(v->goToLogin());
    }

    @Override
    public void startRegister() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishRegister() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedRegister(String message) {
        Toast.makeText(this, "Error on: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showFormError(Integer emailError, Integer passwordError, Integer confPasswordError) {
        mEmailView.setError(emailError == null ? null : getString(emailError));
        mPasswordView.setError(passwordError == null ? null : getString(passwordError));
        mConfPasswordView.setError(confPasswordError == null ? null : getString(confPasswordError));
    }

    @Override
    public void showPasswError() {
        Toast.makeText(this, "Passwords not the same", Toast.LENGTH_LONG).show();
    }

    private void toggleProgressVisibility(final boolean show) {
        mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToSettings() {
        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
