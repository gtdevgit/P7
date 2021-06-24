package com.example.go4lunch.api.firestore;

import com.example.go4lunch.data.firestore.model.User;

public interface UserListener {
    public void onGetUser(User user);
}
