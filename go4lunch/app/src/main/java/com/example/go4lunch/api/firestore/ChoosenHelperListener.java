package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.UserRestaurantAssociation;

import java.util.List;

public interface ChoosenHelperListener {
    public void onGetChoosen(boolean isChoosen);
    public void onFailure(Exception e);
    public void onGetUsersWhoChoseThisRestaurant(List<UserRestaurantAssociation> userRestaurantAssociationList);
}
