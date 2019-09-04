package com.example.photoblog.presentation.view.blank;

import com.arellomobile.mvp.MvpView;
import com.example.photoblog.data.model.BlogPost;
import com.example.photoblog.data.model.User;

import java.util.List;

public interface HomeView extends MvpView {
    void showPosts(List<BlogPost> posts, List<User> users);

    void insertPostItem(BlogPost post, User user);

    void insertMorePostItem(BlogPost post, User user);

    void setFirstPageLoadFalse();

    void showComments(String blogPostId, String currentUserId);

    void clearLists();


//    void insertUserItem(User user);
}
