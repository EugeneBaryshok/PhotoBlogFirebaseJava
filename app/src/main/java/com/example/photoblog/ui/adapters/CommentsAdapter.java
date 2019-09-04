package com.example.photoblog.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.photoblog.R;
import com.example.photoblog.data.model.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comment> commens_list;
    private Context context;

    CommentsAdapter(){}
    public CommentsAdapter(List<Comment> commens_list)
    {
        this.commens_list = commens_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.TvCommentMessage.setText(commens_list.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        if(commens_list!=null) {
            return commens_list.size();
        }
        else return 0;
    }

    public void addComment(Comment comment) {
        commens_list.add(comment);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_message)
        TextView TvCommentMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
