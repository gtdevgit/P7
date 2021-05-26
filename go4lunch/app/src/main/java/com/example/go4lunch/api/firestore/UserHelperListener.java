package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.User;

import java.util.List;

public interface UserHelperListener {
    public void onGetUser(User user);
    public void onGetUsersByList(List<User> users);
    public void onFailure(Exception e);
}
