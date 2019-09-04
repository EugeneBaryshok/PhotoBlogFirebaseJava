package com.example.photoblog.ui.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoblog.R;
import com.example.photoblog.data.model.BlogPost;
import com.example.photoblog.data.model.User;
import com.example.photoblog.presentation.presenter.blank.HomePresenter;
import com.example.photoblog.presentation.view.blank.PostLikeClickListener;
import com.example.photoblog.ui.activity.blank.SetupActivity;
import com.example.photoblog.utils.Images;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private List<BlogPost> blog_list;
    private List<User> users_list;
    private Context context;
    private boolean mMaybeMore;
    private PostLikeClickListener mPostLikeClickListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId = "";


    public void setPosts(List<BlogPost> blogPosts, boolean maybeMore) {
        blog_list = new ArrayList<>(blogPosts);
        dataSetChanged(maybeMore);
    }

    private void dataSetChanged(boolean maybeMore) {
        mMaybeMore = maybeMore;
        notifyDataSetChanged();
    }

    public BlogAdapter(List<BlogPost> blog_list, List<User> users_list, Context context, PostLikeClickListener postLikeClickListener) {
        this.blog_list = blog_list;
        this.users_list = users_list;
        this.context = context;
        this.mPostLikeClickListener = postLikeClickListener;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item, viewGroup, false);
        context = viewGroup.getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        String blogPostId = blog_list.get(i).BlogPostId;
        String blogUserId = blog_list.get(i).getUser_id();

        if(blogUserId.equals(currentUserId)){
//            viewHolder.mPostMenuBtn.setEnabled(true);
            viewHolder.mPostMenuBtn.setVisibility(View.VISIBLE);
        }


        viewHolder.TvBlogDesc.setText(blog_list.get(i).getDesc());
        Images.loadImageWithThumbnail(context, viewHolder.BlogImage, blog_list.get(i).getImage_url(), blog_list.get(i).getImage_thumb());

        long milliseconds = blog_list.get(i).getTimeStamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();

        viewHolder.TvBlogDate.setText(dateString);

        viewHolder.TvBlogUsername.setText(users_list.get(i).getName());
        Images.loadImageWithDefault(context, viewHolder.BlogUserImage, users_list.get(i).getImage());


        firebaseFirestore.collection( "Posts/" + blogPostId + "/Comments").addSnapshotListener((Activity) context, (queryDocumentSnapshots, e) -> {
            if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty()) {
                int count = queryDocumentSnapshots.size();
                viewHolder.updateComments(count);
            } else {
                viewHolder.updateComments(0);
            }
        });
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener((Activity) context, (queryDocumentSnapshots, e) -> {
            if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty()) {
                int count = queryDocumentSnapshots.size();
                viewHolder.updateLikes(count);
            } else {
                viewHolder.updateLikes(0);
            }
        });
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((Activity) context, (documentSnapshot, e) -> {
            if (Objects.requireNonNull(documentSnapshot).exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.mBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_accent));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.mBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.action_like_gray));
                }
            }
        });

        viewHolder.mBlogLikeBtn.setOnClickListener(v -> mPostLikeClickListener.updateLikes(blogPostId, currentUserId));

        viewHolder.mBlogCommentsBtn.setOnClickListener(v -> mPostLikeClickListener.goToComments(blogPostId, currentUserId));

        viewHolder.mPostMenuBtn.setOnClickListener(v -> showPopupMenu(viewHolder.mPostMenuBtn, blogPostId, i));
    }

    private void showPopupMenu(View view, String blogPostId, int position) {
        PopupMenu popup = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(context, view, position);
        }
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(blogPostId, position));
        popup.show();
    }


    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private String blogPostId;
        private int position;
        MyMenuItemClickListener(String blogPostId, int position) {
            this.blogPostId = blogPostId;
            this.position = position;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete_post:
                    firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(task->
                    {
                       blog_list.remove(position);
                       users_list.remove(position);
//                       notifyItemRemoved(position);
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    });


                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.blog_desc)
        TextView TvBlogDesc;
        @BindView(R.id.blog_date)
        TextView TvBlogDate;
        @BindView(R.id.blog_image)
        ImageView BlogImage;
        @BindView(R.id.blog_user_name)
        TextView TvBlogUsername;
        @BindView(R.id.blog_user_image)
        CircleImageView BlogUserImage;
        @BindView(R.id.blog_likes_btn)
        ImageView mBlogLikeBtn;
        @BindView(R.id.blog_comments_btn)
        ImageView mBlogCommentsBtn;
        @BindView(R.id.overflow)
        ImageView mPostMenuBtn;
        @BindView(R.id.blog_likes_count)
        TextView mBlogLikesCount;
        @BindView(R.id.blog_comments_count)
        TextView mBlogCommentsCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void updateLikes(int count) {
            mBlogLikesCount.setText(count + " Likes");
        }
        public void updateComments(int count) {
            mBlogCommentsCount.setText(count + " Comments");
        }
    }

    public void addItem(BlogPost post) {
        blog_list.add(post);
        notifyDataSetChanged();
    }

    public void addItemFirst(BlogPost post) {
        blog_list.add(0, post);
        notifyDataSetChanged();
    }
}
