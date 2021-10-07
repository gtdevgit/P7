package com.example.gtlabgo4lunch.data.firestore.callback_interface;

import com.example.gtlabgo4lunch.data.firestore.model.UidPlaceIdAssociation;

import java.util.List;

public interface UserRestaurantAssociationListListener {
    public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> uidPlaceIdAssociations);
}
