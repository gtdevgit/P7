package com.example.go4lunch.viewmodel;

import android.location.Location;

import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.ui.model.Restaurant;

import java.util.List;

public class MainViewState {
    private final Location location;
    private final List<Restaurant> restaurants;
    private final List<User> workmates;

    public MainViewState(Location location, List<Restaurant> restaurants, List<User> workmates) {
        this.location = location;
        this.restaurants = restaurants;
        this.workmates = workmates;
    }

    public Location getLocation() {
        return location;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<User> getWorkmates() {
        return workmates;
    }
}
