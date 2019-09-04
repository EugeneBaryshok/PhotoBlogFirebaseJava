package com.example.photoblog.ui.fragment.blank;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.photoblog.data.model.BlogPost;
import com.example.photoblog.data.model.User;
import com.example.photoblog.presentation.view.blank.HomeView;
import com.example.photoblog.presentation.presenter.blank.HomePresenter;

import com.arellomobile.mvp.MvpAppCompatFragment;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.photoblog.ui.activity.blank.CommentsActivity;
import com.example.photoblog.ui.adapters.BlogAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends MvpAppCompatFragment implements HomeView {

    public static final String TAG = "HomeFragment";


    private List<BlogPost> blog_list;
    private List<User> users_list;
    private BlogAdapter blogAdapter;

    @BindView(R.id.posts_rv)
    RecyclerView mBlogList;

    @InjectPresenter
    HomePresenter mHomePresenter;

    private boolean isFirstPageFirstLoad = true;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        blog_list = new ArrayList<>();
        users_list = new ArrayList<>();

        showPosts(blog_list, users_list);
        mHomePresenter.getPosts(isFirstPageFirstLoad);
        mBlogList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    mHomePresenter.loadMorePost();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void showPosts(@NonNull List<BlogPost> posts, List<User> users) {
        blogAdapter = new BlogAdapter(posts, users, getContext(), mHomePresenter);
        mBlogList.setAdapter(blogAdapter);
        mBlogList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

//    @Override
//    public void insertUserItem(User user) {
//        users_list.add(user);
//    }

    @Override
    public void insertPostItem(BlogPost post, User user) {
        if (blogAdapter != null) {
            if (isFirstPageFirstLoad) {
                users_list.add(user);
                blog_list.add(post);
//                blogAdapter.addItem(post);
            } else {
                users_list.add(0, user);
                blog_list.add(0, post);
//                blogAdapter.addItemFirst(post);
            }

        }
        if (blogAdapter != null) {
            blogAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void insertMorePostItem(BlogPost post, User user) {
        if (blogAdapter != null) {

            users_list.add(user);
            blog_list.add(post);
            blogAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setFirstPageLoadFalse() {
        isFirstPageFirstLoad = false;
    }

    @Override
    public void showComments(String blogPostId, String currentUserId) {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra("blog_post_id", blogPostId);
        startActivity(intent);
    }

    @Override
    public void clearLists() {
        users_list.clear();
        blog_list.clear();
    }


}
