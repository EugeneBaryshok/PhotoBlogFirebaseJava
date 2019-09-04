package com.example.photoblog.presentation.presenter.blank;


import com.example.photoblog.data.model.BlogPost;
import com.example.photoblog.data.model.User;
import com.example.photoblog.presentation.view.blank.HomeView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.photoblog.presentation.view.blank.PostLikeClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

@InjectViewState
public class HomePresenter extends MvpPresenter<HomeView> implements PostLikeClickListener {
    public static final String TAG = "HomePresenter";
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private boolean mIsFirstLoad = true;
    private ListenerRegistration listenerRegistration;

    @Override
    public void detachView(HomeView view) {
        super.detachView(view);
        listenerRegistration.remove();
    }

    public boolean checkUserIsLogin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            return true;
        else return false;
    }

    public void getPosts(boolean isFirstLoad) {
        mIsFirstLoad = isFirstLoad;
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);
        listenerRegistration = firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                if (mIsFirstLoad) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    getViewState().clearLists();
                }
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String blogPostId = doc.getDocument().getId();
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                        String blog_user_id = doc.getDocument().getString("user_id");
                        firebaseFirestore.collection("Users").document(blog_user_id).get().addOnCompleteListener(task->{
                            if(task.isSuccessful())
                            {
                                User user = task.getResult().toObject(User.class);
//                                getViewState().insertUserItem(user);

                                getViewState().insertPostItem(blogPost, user);
                            }
                            mIsFirstLoad = false;
                            getViewState().setFirstPageLoadFalse();

                        });




                    }
                }
//                mIsFirstLoad = false;
//                getViewState().setFirstPageLoadFalse();
            }
        });
    }
    public void deletePost()
    {

    }

    public void loadMorePost() {
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        listenerRegistration = nextQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty()) {
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String blogPostId = doc.getDocument().getId();

                        String blog_user_id = doc.getDocument().getString("user_id");
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                        firebaseFirestore.collection("Users").document(blog_user_id).get().addOnCompleteListener(task->{
                            if(task.isSuccessful())
                            {
                                User user = task.getResult().toObject(User.class);
//                                getViewState().insertUserItem(user);

                                getViewState().insertMorePostItem(blogPost, user);
                            }

                        });


//                        getViewState().insertPostItem(blogPost);
                    }
                }
            }
        });
    }

    @Override
    public void updateLikes(String blogPostId, String currentUserId) {
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(task -> {
           if(!task.getResult().exists())
           {
               Map<String, Object> likesMap = new HashMap<>();
               likesMap.put("timestamp", FieldValue.serverTimestamp());
               firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likesMap);
           }
           else
           {
               firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
           }
        });

    }

    @Override
    public void goToComments(String blogPostId, String currentUserId) {
        getViewState().showComments(blogPostId, currentUserId);
    }
}

