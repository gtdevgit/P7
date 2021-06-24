package com.example.go4lunch.api.firestore;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;

public interface UserRestaurantAssociationListener {
    public void onGetUserRestaurantAssociation(UidPlaceIdAssociation uidPlaceIdAssociation);
}
