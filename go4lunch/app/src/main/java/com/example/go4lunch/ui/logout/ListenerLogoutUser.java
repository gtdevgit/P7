package com.example.go4lunch.ui.logout;

public interface ListenerLogoutUser {
    void onSuccess();
    void onFailureLogout(String message);
    void onFailureDelete(String message);

}
