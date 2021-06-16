package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.UserRestaurantAssociation;

import java.util.List;

public interface UserRestaurantAssociationListener {
    public void onGetUserRestaurantAssociation(UserRestaurantAssociation userRestaurantAssociation);
}
