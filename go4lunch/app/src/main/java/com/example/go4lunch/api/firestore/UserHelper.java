package com.example.go4lunch.api.firestore;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.data.firestore.model.User;
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

public class UserHelper {
    private static final String TAG = Tag.TAG;
    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static void getUser(String uid, UserListener userListener, FailureListener failureListener){
        getUsersCollection().document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                userListener.onGetUser(user);
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

    public static void getUsersByUidList(List<String> uidList, UserListListener userListListener, FailureListener failureListener){
        List<User> users = new ArrayList<>();
        if ((uidList != null) && (uidList.size() > 0)){
            getUsersCollection()
                    .whereIn("uid", uidList)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User user = document.toObject(User.class);
                                    users.add(user);
                                }
                                Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete(). userList.size = " + users.size());
                                userListListener.onGetUsers(users);
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
        } else {
            userListListener.onGetUsers(users);
        }
    }

    public static Task<Void> deleteUser(String uid) {
        return getUsersCollection().document(uid).delete();
    }

    public static Task<QuerySnapshot> getUsers() {
        return getUsersCollection().get();
    }

}
