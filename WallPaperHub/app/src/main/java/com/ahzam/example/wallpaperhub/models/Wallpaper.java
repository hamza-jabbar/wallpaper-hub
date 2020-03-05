package com.ahzam.example.wallpaperhub.models;

import com.google.firebase.database.Exclude;

public class Wallpaper {

    @Exclude
    public String id;

    public String title, desc, url;

    @Exclude
    public String category;

    //  Check if the image is the users favourite or not
    @Exclude
    public boolean isFavourite = false;

//    public Wallpaper() { }

    public Wallpaper(String id, String title, String desc, String url, String category) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.category = category;
    }
}
