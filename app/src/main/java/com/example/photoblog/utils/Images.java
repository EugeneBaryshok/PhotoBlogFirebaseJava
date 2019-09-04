package com.example.photoblog.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public final class Images {
    private Images() {
    }
    public static void loadImage(Context context, @NonNull ImageView imageView, @NonNull String imageUrl) {
//        Picasso.with(context).load(image_array[position]).placeholder(R.drawable.ic_stub).into(imageView);
        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.image_placeholder);
        Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(imageUrl).into(imageView);
    }
    public static void loadImageWithThumbnail(Context context, @NonNull ImageView imageView, @NonNull String imageUrl, String thumbUrl ) {
//        Picasso.with(context).load(image_array[position]).placeholder(R.drawable.ic_stub).into(imageView);
        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.image_placeholder);
        Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(imageUrl).thumbnail(
                Glide.with(context).load(thumbUrl)
        ).into(imageView);
    }

    public static void loadImageWithDefault(Context context, CircleImageView blogUserImage, String image) {
        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.defaultprofile);
        Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(image).into(blogUserImage);
    }
}