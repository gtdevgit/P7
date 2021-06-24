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
import java.util.List;

public class LikeHelper {
    private static final String TAG = Tag.TAG;

    private static final String COLLECTION_NAME_LIKE = "liked_restaurants";

    /**
     * Liked collection
     *
     * @return
     */
    public static CollectionReference getLikedCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_LIKE);
    }

    /**
     * create unique id for association between uid and placeId composed by [uid]_[placeId]
     *
     * @param uid
     * @param placeId
     * @return
     */
    private static String getDocumentId(String uid, String placeId) {
        return String.format("%s_%s", uid, placeId);
    }

    /**
     * Liked restaurants
     *
     * @param uid
     * @param placeId
     * @return
     */
    public static Task<DocumentSnapshot> getLike(String uid, String placeId) {
        return getLikedCollection().document(getDocumentId(uid, placeId)).get();
    }

    public static void isLiked(String uid, String placeId, LikeHelperListener likeHelperListener) {
        getLike(uid, placeId)
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "LikeHelper.isLiked()->onComplete())");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "LikeHelper.isLiked()->onComplete() isSuccessful = true, document exist = [" + document.exists() + "]");
                        likeHelperListener.onGetLike(document.exists());
                    } else {
                        Log.d(TAG, "LikeHelper.isLiked()->onComplete() isSuccessful=false");
                        Log.d(TAG, "LikeHelper.isLiked()->onComplete() error + " + task.getException());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "LikeHelper.isLiked()->onFailure() called with: e = [" + e + "]");
                }
            });
    }

    public static void createLike(String uid, String placeId, LikeHelperListener likeHelperListener) {
        Log.d(TAG, "createLike() called with: uid = [" + uid + "], placeId = [" + placeId + "]");
        UidPlaceIdAssociation uidPlaceIdAssociation = new UidPlaceIdAssociation(uid, placeId);
        getLikedCollection().document(getDocumentId(uid, placeId)).set(uidPlaceIdAssociation)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    likeHelperListener.onGetLike(true);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "createLike->onFailure() called with: e = [" + e + "]");
                }
            });
    }

    public static void deleteLike(String uid, String placeId, LikeHelperListener likeHelperListener) {
        getLikedCollection().document(getDocumentId(uid, placeId)).delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        likeHelperListener.onGetLike(false);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "deleteLike->onFailure() called with: e = [" + e + "]");
                }
            });
    }

    public static void getLikedRestaurants(UserRestaurantAssociationListListener userRestaurantAssociationListListener, FailureListener failureListener) {
        List<UidPlaceIdAssociation> uidPlaceIdAssociationList = new ArrayList<>();
        getLikedCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                uidPlaceIdAssociationList.add(uidPlaceIdAssociation);
                            }
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

    public static void getUsersWhoLikedThisRestaurant(String placeId, UserRestaurantAssociationListListener userRestaurantAssociationListListener,
                                                      FailureListener failureListener) {
        List<UidPlaceIdAssociation> uidPlaceIdAssociations = new ArrayList<>();
        Log.d(Tag.TAG, "getUsersWhoLikedThisRestaurant");
        getLikedCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(Tag.TAG, "getUsersWhoLikedThisRestaurant.onComplete() ");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                                Log.d(Tag.TAG, "getUsersWhoLikedThisRestaurant.onComplete() userRestaurantAssociation = [" + uidPlaceIdAssociation + "]");
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
}
