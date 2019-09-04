package com.example.photoblog.ui.activity.blank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.arellomobile.mvp.MvpAppCompatActivity;
import com.example.photoblog.presentation.view.blank.MainView;
import com.example.photoblog.presentation.presenter.blank.MainPresenter;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.photoblog.ui.fragment.blank.AccountFragment;
import com.example.photoblog.ui.fragment.blank.HomeFragment;
import com.example.photoblog.ui.fragment.blank.NotificationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends MvpAppCompatActivity implements MainView {
    public static final String TAG = "MainActivity";
    private Unbinder unbinder;

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;

    @InjectPresenter
    MainPresenter mMainPresenter;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.add_post_btn)
    FloatingActionButton mAddPostBtn;

    @BindView(R.id.mainBottomNav)
    BottomNavigationView mMainBottomNav;


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.checkUser();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Photo Blog");

        if (mMainPresenter.checkUserIsLogin()) {
            homeFragment = new HomeFragment();
            accountFragment = new AccountFragment();
            notificationFragment = new NotificationFragment();

            replaceFragment(homeFragment);

            mMainBottomNav.setOnNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_nav_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottom_nav_account:
                        replaceFragment(accountFragment);
                        return true;
                    case R.id.bottom_nav_notification:
                        replaceFragment(notificationFragment);
                        return true;

                }
                return false;
            });

            mAddPostBtn.setOnClickListener(v -> {
                goToNewPost();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                mMainPresenter.logOut();
                return true;
            case R.id.action_settings_btn:
                goToSettings();
            default:
                return false;
        }
    }

    @Override
    public void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToSettings() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToNewPost() {
        Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, "Error on: " + error, Toast.LENGTH_LONG).show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}
