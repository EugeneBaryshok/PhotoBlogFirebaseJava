package com.example.photoblog.app;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.example.photoblog.ui.activity.blank.SetupActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PhotoBlogApp extends Application {
    private static PhotoBlogApp sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        Picasso picasso = new Picasso.Builder(this)
//                .downloader(new OkHttp3Downloader(this))
//                .build();
//        Picasso.setSingletonInstance(picasso);



    }
    @NonNull
    public static PhotoBlogApp getAppContext() {
        return sInstance;
    }

}
