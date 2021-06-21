package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.firestore.User;

public interface UserListener {
    public void onGetUser(User user);
}
