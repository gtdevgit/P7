package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.firestore.UserRestaurantAssociation;

public interface UserRestaurantAssociationListener {
    public void onGetUserRestaurantAssociation(UserRestaurantAssociation userRestaurantAssociation);
}
