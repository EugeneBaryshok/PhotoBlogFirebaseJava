package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

public interface PostLikeClickListener {

    void updateLikes(String blogPostId, String currentUserId);

    void goToComments(String blogPostId, String currentUserId);
}

