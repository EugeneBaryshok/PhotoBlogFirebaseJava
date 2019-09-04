package com.example.photoblog.presentation.presenter.blank;


import com.example.photoblog.presentation.view.blank.MainView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    public void logOut()
    {
        FirebaseAuth.getInstance().signOut();
        getViewState().sendToLogin();
    }
    public boolean checkUserIsLogin()
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null)
            return true;
        else return false;
    }
    public void checkUser()
    {
        String current_user_id;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            getViewState().sendToLogin();
        }
        else
        {
            current_user_id = currentUser.getUid();
            FirebaseFirestore.getInstance().collection("Users").document(current_user_id).get().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    if(!task.getResult().exists())
                    {
                        getViewState().goToSettings();
                    }
                }
                else
                {
                    String errorMessage = task.getException().getMessage();
                    getViewState().showError(errorMessage);
                }
            });
        }
    }
}
