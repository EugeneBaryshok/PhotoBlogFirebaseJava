package com.example.photoblog.ui.fragment.blank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.photoblog.presentation.view.blank.NotificationView;
import com.example.photoblog.presentation.presenter.blank.NotificationPresenter;


import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;

public class NotificationFragment extends MvpAppCompatFragment implements NotificationView {
    public static final String TAG = "NotificationFragment";
    @InjectPresenter
    NotificationPresenter mNotificationPresenter;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
