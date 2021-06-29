package com.example.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.firebase.Authentication;
import com.example.go4lunch.ui.login.LoginActivity;
import com.example.go4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = Tag.TAG;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.setting_activity_coordinator_layout);
        imageViewProfile = findViewById(R.id.content_setting_imageView_profile);
        textViewUserEmail = findViewById(R.id.content_setting_textView_user_email);
        textViewUserName = findViewById(R.id.content_setting_textView_user_name);

        buttonLogout = findViewById(R.id.content_setting_button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUserFromFirebase();
            }
        });

        buttonDeleteUser = findViewById(R.id.content_setting_button_delete_user);
        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserFromFirebase();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        reload();
    }

    private void showSnackBar(String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackBar(getResources().getString(R.string.error_unknown_error) + " " + e.getMessage());
            }
        };
    }

    private void signOutUserFromFirebase(){
        Log.d(TAG, "signOutUserFromFirebase() called");
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted() called with: origin = [" + origin + "]");
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_USERNAME:
                        showSnackBar("User Name updated");
                        //progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        reload();
                        //finish();
                        break;
                    case DELETE_USER_TASK:
                        reload();
                        //finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void reload(){

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "reload() called with: currentUser  = [\" + currentUser + \"]\"); ");
        if(currentUser != null){
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.no_user_email) : currentUser.getEmail();
            //Update views with data
            this.textViewUserEmail.setText(email);

            String username = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.no_user_name_found) : currentUser.getDisplayName();
            this.textViewUserName.setText(username);
        } else {
            // Clear
            Glide.with(this)
                    .load("")
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);
            this.textViewUserEmail.setText("");
            this.textViewUserName.setText(R.string.no_user_connected);

            // Redirect to login activity
            if (!Authentication.isConnected()) {
                Intent intent;
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}