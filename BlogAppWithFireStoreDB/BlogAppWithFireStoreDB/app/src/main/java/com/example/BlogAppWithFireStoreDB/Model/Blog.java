package com.example.BlogAppWithFireStoreDB.Model;

import java.sql.Timestamp;
import java.util.Date;

public class Blog extends BlogPostId {
    public String title;
    public String imageuri;
    //private String image_user;
    public String description;
    public String userid;
    public Date timesstamp;

    public  Blog(){

    }

    public Blog(String userid,String image,String title, String description,Date timesstamp) {

        //this.image_user = image_user;
        this.userid = userid;
        this.timesstamp = timesstamp;
        this.imageuri = image;
        this.title = title;
        this.description = description;

    }
    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri= imageuri;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

   public Date getTimesstamp() {
        return timesstamp;
    }

    public void setTimesstamp(Date timesstamp) {
        this.timesstamp = timesstamp;
    }
  public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   /* public String getImage_user() {
        return image_user;
    }

    public void setImage_user(String image_user) {
        this.image_user = image_user;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
