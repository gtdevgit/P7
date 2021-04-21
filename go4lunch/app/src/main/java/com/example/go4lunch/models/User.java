package com.example.go4lunch.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String uid;
    private String userName;
    private String userEmail;
    private String urlPicture;
    private Date dateCreated;
    private Boolean isLogout;

    public User() {}

    public User(String uid, String userName, String userEmail, String urlPicture) {
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.urlPicture = urlPicture;
        this.isLogout = false;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @ServerTimestamp
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getLogout() {
        return isLogout;
    }

    public void setLogout(Boolean logout) {
        isLogout = logout;
    }
}
