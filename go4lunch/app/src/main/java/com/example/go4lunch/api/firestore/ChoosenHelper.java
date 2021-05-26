package com.example.go4lunch.api.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class ChoosenHelper {
    private static final String TAG = Tag.TAG;

    private static final String COLLECTION_NAME_CHOSEN = "choosen_restaurants";

    // ****************************
    // *** Selected restaurants ***
    // ****************************

    /**
     * Selected collection
     * @return
     */
    private static CollectionReference getChoosenCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_CHOSEN);
    }

    public static void isChoosenRestaurant(String uid, String placeId, ChoosenHelperListener choosenHelperListener) {
        getChoosenCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "ChoosenHelper.isChoosen()->onComplete() isSuccessful = true, document exist = [" + document.exists() + "]");
                            if (document.exists()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                choosenHelperListener.onGetChoosen(userRestaurantAssociation.getPlaceId() == placeId);
                            } else {
                                choosenHelperListener.onGetChoosen(false);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "ChoosenHelper.isChoosen->onFailure() called with: e = [" + e + "]");
                    }
                });
    }

    public static void createChoosenRestaurant(String uid, String placeId, ChoosenHelperListener choosenHelperListener){
        Log.d(TAG, "createChoosenRestaurant() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        UserRestaurantAssociation userRestaurantAssociation = new UserRestaurantAssociation(uid, placeId);
        getChoosenCollection().document(uid).set(userRestaurantAssociation)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    choosenHelperListener.onGetChoosen(true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ChooseRestaurant.onFailure() called with: e = [" + e + "]");
                }
            });
    }

    public static void deleteChoosenRestaurant(String uid, String placeId, ChoosenHelperListener choosenHelperListener) {
        Log.d(TAG, "deleteLike() called with: uid = [" + uid + "], placeId = [" + placeId + "], choosenHelperListener = [" + choosenHelperListener + "]");
        getChoosenCollection().document(uid).delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        choosenHelperListener.onGetChoosen(false);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "deleteLike.onFailure() called with: e = [" + e + "]");
                }
            });
    }
}
