package com.example.go4lunch.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    private static final String TAG = Tag.TAG;
    private MutableLiveData<List<User>> workmates;

    public WorkmatesViewModel() {
        this.workmates = new MutableLiveData<List<User>>();
    }

    public MutableLiveData<List<User>> getWorkmates() {
        loadUsers();
        return workmates;
    }

    public void loadData(){
        loadUsers();
    }

    private void loadUsers(){
        List<User> userList = new ArrayList<>();
        UserHelper.getUsers()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete() called with: task = [" + task + "]");
                        if (task.isSuccessful()){
                            Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete(). isSuccesful called with: task = [" + task + "]");
                            for (QueryDocumentSnapshot user : task.getResult()) {
                                //Log.d(TAG, "" + user);
                                userList.add(user.toObject(User.class));
                            }
                            Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete(). userList.size = " + userList.size());
                            workmates.postValue(userList);
                        } else {
                            Log.d(TAG, "WorkmatesViewModel.getWorkmates().onComplete(). Error getting users list: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "WorkmatesViewModel.getWorkmates().onFailure() called with: e = [" + e + "]");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "WorkmatesViewModel.getWorkmates().onSuccess() called with: queryDocumentSnapshots = [" + queryDocumentSnapshots + "]");
                    }
                });
    }
}
