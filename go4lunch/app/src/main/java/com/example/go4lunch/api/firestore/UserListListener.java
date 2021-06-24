package com.example.go4lunch.api.firestore;

import com.example.go4lunch.data.firestore.model.User;

import java.util.List;

public interface UserListListener {
    public void onGetUsers(List<User> users);
}
