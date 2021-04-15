package com.example.go4lunch.ui.logout;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutViewModel extends ViewModel {

    private MutableLiveData<String> userEmail;
    private MutableLiveData<String> userName;
    private MutableLiveData<Uri> userPictureUri;

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;

    private FirebaseAuth mAuth;
    private CoordinatorLayout coordinatorLayout;
    private ImageView imageViewProfile;
    private TextView textViewUserEmail;
    private TextView textViewUserName;

    private Button buttonLogout;
    private Button buttonDeleteUser;

    public LogoutViewModel() {
        userEmail = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        userPictureUri = new MutableLiveData<>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadData();
    }

    public void loadData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            this.userPictureUri.setValue(null);
            this.textViewUserEmail.setText("");
            this.textViewUserName.setText(R.string.no_user_connected);
        } else {
            //Get email & username from Firebase
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
}