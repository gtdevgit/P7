package com.example.go4lunch.ui.main.model;

import android.net.Uri;

public class CurrentUser {
    String name;
    String email;
    Uri photoUrl;

    public CurrentUser(String name, String email, Uri photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }
}
