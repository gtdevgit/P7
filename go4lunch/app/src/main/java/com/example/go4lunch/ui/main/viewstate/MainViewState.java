package com.example.go4lunch.ui.main.viewstate;

import android.location.Location;

import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.ui.main.model.Restaurant;
import com.example.go4lunch.ui.main.model.Workmate;

import java.util.List;

public class MainViewState {
    private final Location location;
    private final List<Restaurant> restaurants;
    private final List<User> users;
    private final List<Workmate> workmates;

    public MainViewState(Location location, List<Restaurant> restaurants, List<User> users, List<Workmate> workmates) {
        this.location = location;
        this.restaurants = restaurants;
        this.users = users;
        this.workmates = workmates;
    }

    public Location getLocation() {
        return location;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Workmate> getWorkmates() { return workmates; }
}
