package com.example.go4lunch.data.firestore.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.firestore.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FirestoreLikedRepository {

    private static final String COLLECTION_NAME_LIKE = "liked_restaurants";

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<UidPlaceIdAssociation>> likedRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<UidPlaceIdAssociation>> LikedRestaurantsByPlaceIdMutableLiveData = new MutableLiveData<>();

    public LiveData<String> getErrorLiveData() {
        return errorMutableLiveData;
    }

    public LiveData<List<UidPlaceIdAssociation>> getLikedRestaurantsLiveData() {
        return likedRestaurantsMutableLiveData;
    }

    public LiveData<List<UidPlaceIdAssociation>> getLikedRestaurantsByPlaceIdLiveData() {
        return LikedRestaurantsByPlaceIdMutableLiveData;
    }

    /**
     * Liked collection
     *
     * @return
     */
    private static CollectionReference getLikedCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_LIKE);
    }

    public void loadLikedRestaurants(){
        getLikedCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<UidPlaceIdAssociation> likedRestaurants = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                likedRestaurants.add(uidPlaceIdAssociation);
                            }
                            likedRestaurantsMutableLiveData.setValue(likedRestaurants);
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

    public void loadLikedRestaurantsByPlaceId(String placeId){
        getLikedCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<UidPlaceIdAssociation> likedRestaurants = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                likedRestaurants.add(uidPlaceIdAssociation);
                            }
                            LikedRestaurantsByPlaceIdMutableLiveData.setValue(likedRestaurants);
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
}
