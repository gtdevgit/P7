package com.example.go4lunch.models.viewstate;

/**
 * Used by detail restaurant to display the list of workmates, simple with name en picture
 */
public class SimpleUserViewState {
    String name;
    String urlPicture;

    public SimpleUserViewState(String name, String urlPicture) {
        this.name = name;
        this.urlPicture = urlPicture;
    }

    public String getName() {
        return name;
    }

    public String getUrlPicture() {
        return urlPicture;
    }
}
