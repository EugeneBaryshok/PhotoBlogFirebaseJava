package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;
import com.example.photoblog.data.model.Comment;

public interface CommentsView extends MvpView {

    void showError(String error);

    void commentSuccess();

    void addComment(Comment comment);
}
