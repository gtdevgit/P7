package com.example.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.models.User;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class UserHelper {
    private static final String TAG = Tag.TAG;
    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String userName, String userEmail, String urlPicture) {
        // 1 - Create Obj
        User userToCreate = new User(uid, userName, userEmail, urlPicture);

        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    public static Task<Void> logoutUser(String uid) {
        return UserHelper.updateLogout(uid, true);
    }

    private static Task<Void> updateLogout(String uid, Boolean isLogout) {
        return UserHelper.getUsersCollection().document(uid).update("islogout", true);
    }

    public static Task<QuerySnapshot> getUsers() {
        return UserHelper.getUsersCollection().get();
    }

}
