package com.example.go4lunch.ui.logout;

import android.net.Uri;

public class LogoutViewState {
    private final String userEmail;
    private final String userName;
    private final Uri userPictureUri;
    private final Boolean buttonDeleteUserEnabled;
    private final Boolean buttonLogoutUserEnabled;

    public LogoutViewState(String userEmail, String userName, Uri userPictureUri, Boolean buttonDeleteUserEnabled, Boolean buttonLogoutUserEnabled) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPictureUri = userPictureUri;
        this.buttonDeleteUserEnabled = buttonDeleteUserEnabled;
        this.buttonLogoutUserEnabled = buttonLogoutUserEnabled;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public Uri getUserPictureUri() {
        return userPictureUri;
    }

    public Boolean getButtonDeleteUserEnabled() {
        return buttonDeleteUserEnabled;
    }

    public Boolean getButtonLogoutUserEnabled() {
        return buttonLogoutUserEnabled;
    }
}
