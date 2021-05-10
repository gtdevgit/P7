package com.example.go4lunch.ui.logout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogoutViewModel extends ViewModel {

    private static final String TAG = Tag.TAG;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private ListenerLogoutUser listenerLogoutUser;

    private MutableLiveData<String> userEmail;
    private MutableLiveData<String> userName;
    private MutableLiveData<Uri> userPictureUri;

    public LogoutViewModel() {
        userEmail = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        userPictureUri = new MutableLiveData<>();

        loadData();
    }

    public void setListenerLogoutUser(ListenerLogoutUser listenerLogoutUser) {
        this.listenerLogoutUser = listenerLogoutUser;
    }

    public void loadData() {
        Log.d(TAG, "loadData() called");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserHelper.logoutUser(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listenerLogoutUser.onFailureLogout(e.getMessage());
                }
            })
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                    public void onSuccess(Void aVoid) {
                        AuthUI.getInstance()
                            .signOut(context)
                            .addOnSuccessListener(executor, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));

                    }
            });
        }
    }

    public void deleteUserFromFirebase(Context context){
        Log.d(TAG, "deleteUserFromFirebase() called with: context = [" + context + "]");
        Executor executor = Executors.newSingleThreadExecutor();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            UserHelper.deleteUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listenerLogoutUser.onFailureDelete(e.getMessage());
                }
            })
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    AuthUI.getInstance()
                            .delete(context)
                            .addOnSuccessListener(executor, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
                }
            });
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted() called with: origin = [" + origin + "]");
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                    case DELETE_USER_TASK:
                        loadData();
                        listenerLogoutUser.onSuccess();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}