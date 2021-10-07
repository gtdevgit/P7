package com.example.gtlabgo4lunch.data.firestore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gtlabgo4lunch.data.firestore.callback_interface.FailureListener;
import com.example.gtlabgo4lunch.data.firestore.callback_interface.UserRestaurantAssociationListListener;
import com.example.gtlabgo4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.gtlabgo4lunch.data.firestore.validation.CurrentTimeLimits;
import com.example.gtlabgo4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirestoreChosenRepository {
    private static final String COLLECTION_NAME_CHOSEN = "chosen_restaurants";

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    public LiveData<String> getErrorLiveData() {
        return errorMutableLiveData;
    }

    private final MutableLiveData<List<UidPlaceIdAssociation>> chosenRestaurantsMutableLiveData = new MutableLiveData<>();
    public LiveData<List<UidPlaceIdAssociation>> getChosenRestaurantsLiveData() {
        return chosenRestaurantsMutableLiveData;
    }

    private final MutableLiveData<List<UidPlaceIdAssociation>> chosenRestaurantsByPlaceIdMutableLiveData = new MutableLiveData<>();
    public LiveData<List<UidPlaceIdAssociation>> getChosenRestaurantsByPlaceIdLiveData() {
        return chosenRestaurantsByPlaceIdMutableLiveData;
    }

    private ListenerRegistration registrationChosenByPlaceId;
    private ListenerRegistration registrationChosenAll;

    /**
     * Selected collection
     * @return
     */
    public CollectionReference getChosenCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_CHOSEN);
    }

    public void loadAllChosenRestaurants(){
        CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();
        getChosenCollection()
                .whereGreaterThanOrEqualTo("createdTime", currentTimeLimits.getLowLimit())
                .whereLessThanOrEqualTo("createdTime", currentTimeLimits.getHighLimit())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<UidPlaceIdAssociation> chosenRestaurants = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                chosenRestaurants.add(uidPlaceIdAssociation);
                            }
                            chosenRestaurantsMutableLiveData.setValue(chosenRestaurants);
                        } else {
                            if (task.getException() != null) {
                                errorMutableLiveData.setValue(task.getException().getMessage());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        errorMutableLiveData.setValue(e.getMessage());
                    }
                });
    }

    public void loadChosenRestaurantsByPlaceId(String placeId){
        getChosenCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<UidPlaceIdAssociation> chosenRestaurants = new ArrayList<>();
                            CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                if (currentTimeLimits.isValidDate(uidPlaceIdAssociation.getCreatedTime())) {
                                    chosenRestaurants.add(uidPlaceIdAssociation);
                                }
                            }
                            chosenRestaurantsByPlaceIdMutableLiveData.setValue(chosenRestaurants);
                        } else {
                            if (task.getException() != null) {
                                errorMutableLiveData.setValue(task.getException().getMessage());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        errorMutableLiveData.setValue(e.getMessage());
                    }
                });
    }

    public void createChosenRestaurant(String uid, String placeId){
        UidPlaceIdAssociation uidPlaceIdAssociation = new UidPlaceIdAssociation(uid, placeId);
        getChosenCollection().document(uid).set(uidPlaceIdAssociation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null) {
                                errorMutableLiveData.setValue(task.getException().getMessage());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag.TAG, "createChosenRestaurant.onFailure() called with: e = [" + e + "]");
                        errorMutableLiveData.setValue(e.getMessage());
                    }
                });
    }

    public void deleteChosenRestaurant(String uid){
        getChosenCollection().document(uid).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null) {
                                errorMutableLiveData.setValue(task.getException().getMessage());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag.TAG, "deleteLike.onFailure() called with: e = [" + e + "]");
                        errorMutableLiveData.setValue(e.getMessage());
                    }
                });
    }

    public void getChosenRestaurants(UserRestaurantAssociationListListener userRestaurantAssociationListListener,
                                     FailureListener failureListener) {
        List<UidPlaceIdAssociation> uidPlaceIdAssociationList = new ArrayList<>();
        Log.d(Tag.TAG, "getChosenRestaurants");
        getChosenCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                if (currentTimeLimits.isValidDate(uidPlaceIdAssociation.getCreatedTime())) {
                                    uidPlaceIdAssociationList.add(uidPlaceIdAssociation);
                                }
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

    public void activateRealTimeChosenByPlaceListener(String placeId){
        registrationChosenByPlaceId = getChosenCollection()
                .whereEqualTo("placeId", placeId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            errorMutableLiveData.postValue(error.getMessage());
                            return;
                        }
                        List<UidPlaceIdAssociation> uidPlaceIdAssociations = new ArrayList<>();
                        CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();
                        for (QueryDocumentSnapshot document : value){
                            UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                            if (currentTimeLimits.isValidDate(uidPlaceIdAssociation.getCreatedTime())){
                                uidPlaceIdAssociations.add(uidPlaceIdAssociation);
                            }
                        }
                        chosenRestaurantsByPlaceIdMutableLiveData.setValue(uidPlaceIdAssociations);
                    }
                });
    }

    public void removeRealTimeChosenByPlaceListener(){
        if (registrationChosenByPlaceId != null) {
            registrationChosenByPlaceId.remove();
        }
    }

    public void activateRealTimeChosenListener(){
        CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();
        registrationChosenAll = getChosenCollection()
                .whereGreaterThanOrEqualTo("createdTime", currentTimeLimits.getLowLimit())
                .whereLessThanOrEqualTo("createdTime", currentTimeLimits.getHighLimit())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            errorMutableLiveData.postValue(error.getMessage());
                            return;
                        }
                        List<UidPlaceIdAssociation> uidPlaceIdAssociations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value){
                            UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                            uidPlaceIdAssociations.add(uidPlaceIdAssociation);
                        }
                        chosenRestaurantsMutableLiveData.setValue(uidPlaceIdAssociations);
                    }
                });
    }

    public void removeRealTimeChosenListener(){
        if (registrationChosenAll != null) {
            registrationChosenAll.remove();
        }
    }
}
