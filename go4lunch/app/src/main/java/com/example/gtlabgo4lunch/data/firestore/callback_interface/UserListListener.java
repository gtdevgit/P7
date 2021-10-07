package com.example.gtlabgo4lunch.data.firestore.callback_interface;

import com.example.gtlabgo4lunch.data.firestore.model.User;

import java.util.List;

public interface UserListListener {
    public void onGetUsers(List<User> users);
}
