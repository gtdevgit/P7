package com.example.go4lunch.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<List<User>> workmatesMutableLiveData = new MutableLiveData<>();
    private ListenerRegistration registrationUsers;

    public WorkmatesViewModel() {
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return workmatesMutableLiveData;
    }

    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    public void loadWorkmates(){
        List<User> users = new ArrayList<>();
        UserHelper.getUsers()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onComplete() called with: task = [" + task + "]");
                        if (task.isSuccessful()){
                            Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onComplete(). isSuccesful called with: task = [" + task + "]");
                            for (QueryDocumentSnapshot user : task.getResult()) {
                                //Log.d(TAG, "" + user);
                                users.add(user.toObject(User.class));
                            }
                            Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onComplete(). users.size = " + users.size());
                            workmatesMutableLiveData.postValue(users);
                        } else {
                            Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onComplete(). Error getting users list: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onFailure() called with: e = [" + e + "]");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(Tag.TAG, "WorkmatesViewModel.getWorkmates().onSuccess() called with: queryDocumentSnapshots = [" + queryDocumentSnapshots + "]");
                    }
                });
    }

    /**
     * to get real time change in users list
     */
    public void activateWorkmatesListener(){
        Log.d(Tag.TAG, "WorkmatesViewModel.activateUsersListener() called");
        registrationUsers = UserHelper.getUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                workmatesMutableLiveData.postValue(users);
            }
        });
    }

    public void removeWorkmatesListener(){
        if (registrationUsers != null) {
            Log.d(Tag.TAG, "WorkmatesViewModel.removeUsersListener() called");
            registrationUsers.remove();
        }
    }
}
