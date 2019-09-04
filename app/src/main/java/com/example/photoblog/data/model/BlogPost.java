package com.example.photoblog.data.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class BlogPost extends BlogPostId{
    private String image_thumb, image_url, desc, user_id;
    private Date timestamp;

    public BlogPost() {}
    public BlogPost(String image_thumb, String image_url, String desc, String user_id, Date timestamp) {
        this.image_thumb = image_thumb;
        this.image_url = image_url;
        this.desc = desc;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
