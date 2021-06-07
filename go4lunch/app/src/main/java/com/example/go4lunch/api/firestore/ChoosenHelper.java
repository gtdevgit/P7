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

    public static void isChoosenRestaurant(String uid, String placeId, ChoosenListener choosenListener, FailureListener failureListener) {
        getChoosenCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(Tag.TAG, "ChoosenHelper.isChoosen()->onComplete() isSuccessful = true, document exist = [" + document.exists() + "]");
                            if (document.exists()) {
                                UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                                Log.d(Tag.TAG, "ChoosenHelper.isChoosen()->onComplete() plaseId ok =" + (userRestaurantAssociation.getPlaceId() == placeId));
                                // must check if si this the good palce
                                choosenListener.onGetChoosen(userRestaurantAssociation.getPlaceId().equals(placeId));
                            } else {
                                Log.d(Tag.TAG, "ChoosenHelper.isChoosen()->onComplete() false");
                                choosenListener.onGetChoosen(false);
                            }
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag.TAG, "ChoosenHelper.isChoosen->onFailure() called with: e = [" + e + "]");
                        failureListener.onFailure(e);
                    }
                });
    }

    public static void createChoosenRestaurant(String uid, String placeId, ChoosenListener choosenListener, FailureListener failureListener){
        Log.d(Tag.TAG, "createChoosenRestaurant() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        UserRestaurantAssociation userRestaurantAssociation = new UserRestaurantAssociation(uid, placeId);
        getChoosenCollection().document(uid).set(userRestaurantAssociation)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        choosenListener.onGetChoosen(true);
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(Tag.TAG, "ChooseRestaurant.onFailure() called with: e = [" + e + "]");
                    failureListener.onFailure(e);
                }
            });
    }

    public static void deleteChoosenRestaurant(String uid, String placeId, ChoosenListener choosenListener, FailureListener failureListener) {
        Log.d(Tag.TAG, "deleteLike() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        getChoosenCollection().document(uid).delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        choosenListener.onGetChoosen(false);
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
        getChoosenCollection()
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

    public static void getChoosenRestaurants(UserRestaurantAssociationListListener userRestaurantAssociationListListener, FailureListener failureListener) {
        List<UserRestaurantAssociation> userRestaurantAssociationList = new ArrayList<>();
        Log.d(Tag.TAG, "getChoosenRestaurants");
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
                            Log.d(Tag.TAG, "getChoosenRestaurants. successful with userRestaurantAssociationList.size()=" + userRestaurantAssociationList.size());
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
