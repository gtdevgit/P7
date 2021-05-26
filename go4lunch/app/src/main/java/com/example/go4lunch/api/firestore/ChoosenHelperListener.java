package com.example.go4lunch.api.firestore;

import com.example.go4lunch.models.UserRestaurantAssociation;

public interface ChoosenHelperListener {
    public void onGetChoosen(boolean isChoosen);
}
