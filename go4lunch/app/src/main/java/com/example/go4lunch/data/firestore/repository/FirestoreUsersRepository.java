package com.example.go4lunch.data.firestore.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.tag.Tag;
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

public class FirestoreUsersRepository {
    private static final String COLLECTION_NAME = "users";

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> usersByUidsMutableLiveData = new MutableLiveData<>();

    public LiveData<String> getErrorLiveData() {
        return errorMutableLiveData;
    }
    public LiveData<List<User>> getUsersLiveData() {
        return usersMutableLiveData;
    }
    public LiveData<List<User>> getUsersByUidsMutableLiveData() { return usersByUidsMutableLiveData; }

    private ListenerRegistration registrationUsers;

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void loadAllUsers(){
        getUsersCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<User> users = new ArrayList<>();
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot){
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            usersMutableLiveData.setValue(users);
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

    public void activeRealTimeListener(){
        registrationUsers = getUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null){
                    errorMutableLiveData.postValue(error.getMessage());
                    return;
                }
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                }
                usersMutableLiveData.setValue(users);
            }
        });
    }

    public void removeRealTimeListener(){
        if (registrationUsers != null) {
            registrationUsers.remove();
        }
    }

    public void loadUsersByUids(List<String> uids){
        List<User> users = new ArrayList<>();

        if ((uids != null) && (uids.size() > 0)) {
            getUsersCollection()
                    .whereIn("uid", uids)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    User user = document.toObject(User.class);
                                    users.add(user);
                                }
                                usersByUidsMutableLiveData.setValue(users);
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
        } else
            // empty data
            usersByUidsMutableLiveData.setValue(users);
    }
}

