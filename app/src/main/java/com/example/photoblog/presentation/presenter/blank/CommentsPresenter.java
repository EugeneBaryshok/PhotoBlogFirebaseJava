package com.example.photoblog.presentation.presenter.blank;


import com.example.photoblog.data.model.BlogPost;
import com.example.photoblog.data.model.Comment;
import com.example.photoblog.presentation.view.blank.CommentsView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

@InjectViewState
public class CommentsPresenter extends MvpPresenter<CommentsView> {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private String blog_post_id;

    public void init(String blog_post_id) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        this.blog_post_id = blog_post_id;
        getComments();
    }

    public void getComments() {
        Query firstQuery = firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments")
                .orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        Comment comment = doc.getDocument().toObject(Comment.class);
                        getViewState().addComment(comment);
//                        commentList.add(comment);

//                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
//                        getViewState().insertPostItem(blogPost);

                    }
                }
            }
        });
    }

    public void sendComment(String comment) {
        Map<String, Object> commentsMap = new HashMap<>();
        commentsMap.put("message", comment);
        commentsMap.put("user_id", current_user_id);
        commentsMap.put("timestamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(task ->
        {
            if (!task.isSuccessful()) {
                getViewState().showError(task.getException().getMessage());
            } else {
                getViewState().commentSuccess();
            }
        });
    }

}
