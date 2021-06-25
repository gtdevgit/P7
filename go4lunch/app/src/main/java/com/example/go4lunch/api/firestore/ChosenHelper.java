package com.example.go4lunch.api.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
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
import java.util.Calendar;
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
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                Log.d(Tag.TAG, "ChosenHelper.isChosen()->onComplete() plaseId ok =" + (uidPlaceIdAssociation.getPlaceId() == placeId));
                                // must check if is this the good place
                                chosenListener.onGetChosen(uidPlaceIdAssociation.getPlaceId().equals(placeId));
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

    public static void getUsersWhoChoseThisRestaurant(String placeId, UserRestaurantAssociationListListener userRestaurantAssociationListListener,
                                                      FailureListener failureListener) {
        List<UidPlaceIdAssociation> uidPlaceIdAssociations = new ArrayList<>();
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
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                Log.d(Tag.TAG, "getUsersWhoChoseThisRestaurant.onComplete() userRestaurantAssociation = [" + uidPlaceIdAssociation + "]");
                                uidPlaceIdAssociations.add(document.toObject(UidPlaceIdAssociation.class));
                            }
                            userRestaurantAssociationListListener.onGetUserRestaurantAssociationList(uidPlaceIdAssociations);
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
        List<UidPlaceIdAssociation> uidPlaceIdAssociationList = new ArrayList<>();
        Log.d(Tag.TAG, "getChosenRestaurants");
        getChosenCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                uidPlaceIdAssociationList.add(document.toObject(UidPlaceIdAssociation.class));
                            }
                            Log.d(Tag.TAG, "getChosenRestaurants. successful with userRestaurantAssociationList.size()=" + uidPlaceIdAssociationList.size());
                            userRestaurantAssociationListListener.onGetUserRestaurantAssociationList(uidPlaceIdAssociationList);
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

    public static void getChosenRestaurantByUser(String uid, UserRestaurantAssociationListener userRestaurantAssociationListener, FailureListener failureListener){
        Log.d(Tag.TAG, "getChosenRestaurantByUser() called with: uid = [" + uid + "]");
        getChosenCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        Log.d(Tag.TAG, "getChosenRestaurantByUser() onComplete() task.isSuccessful()=" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Log.d(Tag.TAG, "getChosenRestaurantByUser() onComplete() documentSnapshot.exists()=" + documentSnapshot.exists());
                            if (documentSnapshot.exists()){
                                UidPlaceIdAssociation uidPlaceIdAssociation = documentSnapshot.toObject(UidPlaceIdAssociation.class);
                                // test la date
                                long createdTime = uidPlaceIdAssociation.getCreatedTime();
                                // current time
                                Calendar firstHourOffTheDay = Calendar.getInstance();
                                // current day at 00h00
                                firstHourOffTheDay.set(Calendar.HOUR, 0);
                                firstHourOffTheDay.set(Calendar.MINUTE, 0);
                                firstHourOffTheDay.set(Calendar.SECOND, 0);
                                firstHourOffTheDay.set(Calendar.MILLISECOND, 0);
                                // current day at 23h59m59s999
                                Calendar lastHourOffTheDay = Calendar.getInstance();
                                lastHourOffTheDay.set(Calendar.HOUR, 23);
                                lastHourOffTheDay.set(Calendar.MINUTE, 59);
                                lastHourOffTheDay.set(Calendar.SECOND, 59);
                                lastHourOffTheDay.set(Calendar.MILLISECOND, 999);

                                // createdTime must be in current day
                                if ((createdTime >= firstHourOffTheDay.getTimeInMillis()) &&
                                        (createdTime <= lastHourOffTheDay.getTimeInMillis())){
                                    userRestaurantAssociationListener.onGetUserRestaurantAssociation(uidPlaceIdAssociation);
                                } else {
                                    Log.d(Tag.TAG, "onComplete() bad time !");
                                }
                            }
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
