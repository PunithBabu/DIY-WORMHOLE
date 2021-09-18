package com.babuminiproject.diywormhole;

import android.net.Uri;

public class Posts {

   public String date,description,fullname,ideaprice,postimage,profileimage,productprice,time,title,uid;

    public Posts() {
    }

    public Posts(String date, String description, String fullname, String ideaprice, String postimage, String productprice, String profileimage, String time, String title, String uid) {
        this.date = date;
        this.description = description;
        this.fullname = fullname;
        this.ideaprice = ideaprice;
        this.postimage = postimage;
        this.productprice = productprice;
        this.profileimage = profileimage;
        this.time = time;
        this.title = title;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getIdeaprice() {
        return ideaprice;
    }

    public void setIdeaprice(String ideaprice) {
        this.ideaprice = ideaprice;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
