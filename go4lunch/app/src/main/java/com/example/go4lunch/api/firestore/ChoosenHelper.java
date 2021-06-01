package com.example.go4lunch.api.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.models.User;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    public static CollectionReference getChoosenCollection() {
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
                                Log.d(TAG, "ChoosenHelper.isChoosen()->onComplete() plaseId ok =" + (userRestaurantAssociation.getPlaceId() == placeId));
                                // must check if si this the good palce
                                choosenHelperListener.onGetChoosen(userRestaurantAssociation.getPlaceId().equals(placeId));
                            } else {
                                Log.d(TAG, "ChoosenHelper.isChoosen()->onComplete() false");
                                choosenHelperListener.onGetChoosen(false);
                            }
                        } else {
                            choosenHelperListener.onFailure(task.getException());
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
                    if (task.isSuccessful()) {
                        choosenHelperListener.onGetChoosen(true);
                    } else {
                        choosenHelperListener.onFailure(task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "ChooseRestaurant.onFailure() called with: e = [" + e + "]");
                    choosenHelperListener.onFailure(e);
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
                    } else {
                        choosenHelperListener.onFailure(task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "deleteLike.onFailure() called with: e = [" + e + "]");
                    choosenHelperListener.onFailure(e);
                }
            });
    }

    public static void getUsersWhoChoseThisRestaurant(String placeId, ChoosenHelperListener choosenHelperListener) {
        List<UserRestaurantAssociation> userRestaurantAssociationList = new ArrayList<>();
        Log.d(TAG, "getUsersWhoChoseThisRestaurant: ");
        getChoosenCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "getUsersWhoChoseThisRestaurant.onComplete() ");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                Log.d(TAG, "getUsersWhoChoseThisRestaurant.onComplete() userRestaurantAssociation = [" + userRestaurantAssociation + "]");
                                userRestaurantAssociationList.add(document.toObject(UserRestaurantAssociation.class));
                            }
                            choosenHelperListener.onGetUsersWhoChoseThisRestaurant(userRestaurantAssociationList);
                        } else {
                            choosenHelperListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        choosenHelperListener.onFailure(e);
                    }
                });
    }

    public static void getChoosenRestaurants(ChoosenHelperListener choosenHelperListener) {
        List<UserRestaurantAssociation> userRestaurantAssociationList = new ArrayList<>();
        Log.d(TAG, "getChoosenRestaurants: ");
        getChoosenCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                userRestaurantAssociationList.add(document.toObject(UserRestaurantAssociation.class));
                            }
                            choosenHelperListener.onGetChoosenRestaurants(userRestaurantAssociationList);
                        } else {
                            choosenHelperListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        choosenHelperListener.onFailure(e);
                    }
                });
    }
}
