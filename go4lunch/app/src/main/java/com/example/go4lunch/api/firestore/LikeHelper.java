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
        UserRestaurantAssociation userRestaurantAssociation = new UserRestaurantAssociation(uid, placeId);
        getLikedCollection().document(getDocumentId(uid, placeId)).set(userRestaurantAssociation)
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
}
