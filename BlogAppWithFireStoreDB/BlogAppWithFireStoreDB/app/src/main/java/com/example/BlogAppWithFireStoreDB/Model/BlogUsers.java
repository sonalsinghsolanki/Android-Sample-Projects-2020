package com.example.BlogAppWithFireStoreDB.Model;

public class BlogUsers {
    public String image,name;

    public BlogUsers() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlogUsers(String image, String name) {
        this.image = image;
        this.name = name;
    }
}
