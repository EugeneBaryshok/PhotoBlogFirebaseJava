package com.example.photoblog.ui.activity.blank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoblog.data.model.Comment;
import com.example.photoblog.presentation.view.blank.CommentsView;
import com.example.photoblog.presentation.presenter.blank.CommentsPresenter;

import com.arellomobile.mvp.MvpAppCompatActivity;

import com.example.photoblog.R;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.photoblog.ui.adapters.CommentsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommentsActivity extends MvpAppCompatActivity implements CommentsView {
    public static final String TAG = "CommentsActivity";
    private Unbinder unbinder;
    @InjectPresenter
    CommentsPresenter mCommentsPresenter;

    @BindView(R.id.comments_toolbar)
    Toolbar mCommentsToolbar;

    @BindView(R.id.comment_field)
    EditText mCommentField;

    @BindView(R.id.comments_rv)
    RecyclerView mCommentsRV;

    @BindView(R.id.comment_post_btn)
    ImageView mCommentsPostBtn;

    private String blog_post_id;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList = new ArrayList<>();


    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, CommentsActivity.class);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mCommentsToolbar);
        getSupportActionBar().setTitle("Comments");

        blog_post_id = getIntent().getStringExtra("blog_post_id");
        mCommentsPresenter.init(blog_post_id);

        commentsAdapter = new CommentsAdapter(commentList);
        mCommentsRV.setHasFixedSize(true);
        mCommentsRV.setLayoutManager(new LinearLayoutManager(this));
        mCommentsRV.setAdapter(commentsAdapter);




        mCommentsPostBtn.setOnClickListener(v -> {
           String comment_message = mCommentField.getText().toString();
           if(!comment_message.isEmpty())
           {
                mCommentsPresenter.sendComment(comment_message);
           }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, "Error on posting comment: "+error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void commentSuccess() {
        mCommentField.setText("");
    }

    @Override
    public void addComment(Comment comment) {
        if(commentsAdapter!=null)
        {
            commentsAdapter.addComment(comment);
        }
    }
}
