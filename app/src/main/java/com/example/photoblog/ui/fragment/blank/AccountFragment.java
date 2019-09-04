package com.example.photoblog.ui.fragment.blank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoblog.presentation.view.blank.AccountView;
import com.example.photoblog.presentation.presenter.blank.AccountPresenter;

import com.arellomobile.mvp.MvpAppCompatFragment;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;

public class AccountFragment extends MvpAppCompatFragment implements AccountView {
    public static final String TAG = "AccountFragment";
    @InjectPresenter
    AccountPresenter mAccountPresenter;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
