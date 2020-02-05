package com.example.simpleblogapp;

public class Blog {
    private String title,imageurl,description;

    public  Blog(){

    }
    public Blog(String title, String imageurl, String description) {
        this.title = title;
        this.imageurl = imageurl;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
