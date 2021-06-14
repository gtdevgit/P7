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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChosenHelper {

    private static final String COLLECTION_NAME_CHOSEN = "chosen_restaurants";

    // ****************************
    // *** Selected restaurants ***
    // ****************************

    /**
     * Selected collection
     * @return
     */
    public static CollectionReference getChosenCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_CHOSEN);
    }

    public static void isChosenRestaurant(String uid, String placeId, ChosenListener chosenListener, FailureListener failureListener) {
        getChosenCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(Tag.TAG, "ChosenHelper.isChosen()->onComplete() isSuccessful = true, document exist = [" + document.exists() + "]");
                            if (document.exists()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                Log.d(Tag.TAG, "ChosenHelper.isChosen()->onComplete() plaseId ok =" + (userRestaurantAssociation.getPlaceId() == placeId));
                                // must check if is this the good place
                                chosenListener.onGetChosen(userRestaurantAssociation.getPlaceId().equals(placeId));
                            } else {
                                Log.d(Tag.TAG, "ChosenHelper.isChosen()->onComplete() false");
                                chosenListener.onGetChosen(false);
                            }
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag.TAG, "ChosenHelper.isChosen->onFailure() called with: e = [" + e + "]");
                        failureListener.onFailure(e);
                    }
                });
    }

    public static void createChosenRestaurant(String uid, String placeId, ChosenListener chosenListener, FailureListener failureListener){
        Log.d(Tag.TAG, "createChosenRestaurant() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        UserRestaurantAssociation userRestaurantAssociation = new UserRestaurantAssociation(uid, placeId);
        getChosenCollection().document(uid).set(userRestaurantAssociation)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        chosenListener.onGetChosen(true);
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(Tag.TAG, "createChosenRestaurant.onFailure() called with: e = [" + e + "]");
                    failureListener.onFailure(e);
                }
            });
    }

    public static void deleteChosenRestaurant(String uid, String placeId, ChosenListener chosenListener, FailureListener failureListener) {
        Log.d(Tag.TAG, "deleteLike() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        getChosenCollection().document(uid).delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        chosenListener.onGetChosen(false);
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(Tag.TAG, "deleteLike.onFailure() called with: e = [" + e + "]");
                    failureListener.onFailure(e);
                }
            });
    }

    public static void getUsersWhoChoseThisRestaurant(String placeId, UserRestaurantAssociationListListener userRestaurantAssociationListListener,
                                                      FailureListener failureListener) {
        List<UserRestaurantAssociation> userRestaurantAssociations = new ArrayList<>();
        Log.d(Tag.TAG, "getUsersWhoChoseThisRestaurant: ");
        getChosenCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(Tag.TAG, "getUsersWhoChoseThisRestaurant.onComplete() ");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                Log.d(Tag.TAG, "getUsersWhoChoseThisRestaurant.onComplete() userRestaurantAssociation = [" + userRestaurantAssociation + "]");
                                userRestaurantAssociations.add(document.toObject(UserRestaurantAssociation.class));
                            }
                            userRestaurantAssociationListListener.onGetUserRestaurantAssociationList(userRestaurantAssociations);
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        failureListener.onFailure(e);
                    }
                });
    }

    public static void getChosenRestaurants(UserRestaurantAssociationListListener userRestaurantAssociationListListener, FailureListener failureListener) {
        List<UserRestaurantAssociation> userRestaurantAssociationList = new ArrayList<>();
        Log.d(Tag.TAG, "getChosenRestaurants");
        getChosenCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                userRestaurantAssociationList.add(document.toObject(UserRestaurantAssociation.class));
                            }
                            Log.d(Tag.TAG, "getChosenRestaurants. successful with userRestaurantAssociationList.size()=" + userRestaurantAssociationList.size());
                            userRestaurantAssociationListListener.onGetUserRestaurantAssociationList(userRestaurantAssociationList);
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        failureListener.onFailure(e);
                    }
                });
    }
}
