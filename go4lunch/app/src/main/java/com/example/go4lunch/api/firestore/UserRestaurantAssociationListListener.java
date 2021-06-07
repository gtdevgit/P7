package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.UserRestaurantAssociation;

import java.util.List;

public interface UserRestaurantAssociationListListener {
    public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations);
}
