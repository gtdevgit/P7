package com.example.go4lunch.ui.main.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class SearchViewResultItem {
    String description;
    String placeId;

    public SearchViewResultItem(String description, String placeId) {
        this.description = description;
        this.placeId = placeId;
    }

    public String getDescription() {
        return description;
    }

    public String getPlaceId() {
        return placeId;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return getDescription();
    }
}
