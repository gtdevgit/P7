package com.example.go4lunch.api.firestore;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;

import java.util.List;

public interface UserRestaurantAssociationListListener {
    public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> uidPlaceIdAssociations);
}
