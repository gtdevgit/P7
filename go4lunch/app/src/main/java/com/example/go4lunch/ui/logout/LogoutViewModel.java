package com.example.go4lunch.ui.logout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogoutViewModel extends ViewModel {

    private static final String TAG = "LogoutFragment";
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private ListenerLogoutUser listenerLogoutUser;

    private MutableLiveData<String> userEmail;
    private MutableLiveData<String> userName;
    private MutableLiveData<Uri> userPictureUri;

    private FirebaseAuth mAuth;

    public LogoutViewModel() {
        userEmail = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        userPictureUri = new MutableLiveData<>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadData();
    }

    public void setListenerLogoutUser(ListenerLogoutUser listenerLogoutUser) {
        this.listenerLogoutUser = listenerLogoutUser;
    }

    public void loadData() {
        Log.d(TAG, "loadData() called");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d(TAG, "loadData() current user == null");
            // use postValue instead of setValue to avoid error
            // "IllegalStateException: Cannot invoke setValue on a background thread"
            // when logout callback
            userEmail.postValue("");
            userName.postValue("");
            this.userPictureUri.postValue(Uri.EMPTY);
        } else {
            //Get email & username from Firebase
            Log.d(TAG, "loadData() current user = " + currentUser);
            String email = currentUser.getEmail();
            this.userEmail.setValue(email);
            String name = currentUser.getDisplayName();
            this.userName.setValue(name);
            this.userPictureUri.setValue(currentUser.getPhotoUrl());
        }
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }
    public LiveData<String> getUserName() {
        return userName;
    }
    public LiveData<Uri> getUserPictureUri() { return userPictureUri; }

    public void signOutUserFromFirebase(Context context){
        Executor executor = Executors.newSingleThreadExecutor();

        AuthUI.getInstance()
                .signOut(context)
                .addOnSuccessListener(executor, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    public void deleteUserFromFirebase(Context context){
        Executor executor = Executors.newSingleThreadExecutor();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            AuthUI.getInstance()
                    .delete(context)
                    .addOnSuccessListener(executor, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        loadData();
                        listenerLogoutUser.onSuccessLogoutUser();
                        break;
                    case DELETE_USER_TASK:
                        loadData();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}