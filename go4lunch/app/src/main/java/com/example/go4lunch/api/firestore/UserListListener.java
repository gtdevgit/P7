package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.User;

import java.util.List;

public interface UserListListener {
    public void onGetUsers(List<User> users);
}
