package com.example.go4lunch.api.firestore;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.go4lunch.models.User;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    public static void getUser(String uid, UserHelperListener userHelperListener){
        UserHelper.getUsersCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                userHelperListener.onGetUser(user);
                            } else
                            {
                                userHelperListener.onErrorMessage("No such user document");
                            }
                        } else {
                            userHelperListener.onErrorMessage(task.getException().getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        userHelperListener.onErrorMessage(e.getMessage());
                    }
                });
    }

    public static void getUsersByList(List<String> uidList, UserHelperListener userHelperListener){
        List<User> users = new ArrayList<>();
        Log.d(TAG, "UserHelper.getUsersByList() called with: uidList = [" + uidList + "], userHelperListener = [" + userHelperListener + "]");
        if ((uidList != null) && (uidList.size() > 0)){
            UserHelper.getUsersCollection()
                    .whereIn("uid", uidList)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User user = document.toObject(User.class);
                                    Log.d(TAG, "UserHelper.getUsersByList() user = " + user.getUserEmail());
                                    users.add(user);
                                }
                                Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete(). userList.size = " + users.size());
                                userHelperListener.onGetUsersByList(users);
                            } else {
                                userHelperListener.onErrorMessage(task.getException().getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                userHelperListener.onErrorMessage(e.getMessage());
                            }
                    });
        } else {
            userHelperListener.onGetUsersByList(users);
        }
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

    public static void test(){

        CollectionReference colRef = UserHelper.getUsersCollection();
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e(TAG, "onEvent: " + error.getMessage() );
                    return;
                }
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                }

            }
        });


    }

}
