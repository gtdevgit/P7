package com.example.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private Button buttonEmail;
    private Button buttonGoogle;
    private Button buttonFacebook;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.setting_activity_coordinator_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        buttonEmail = findViewById(R.id.setting_activity_button_login_email);
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.EMAIL);
            }
        });

        buttonGoogle = findViewById(R.id.setting_activity_button_login_google);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.GOOGLE);
            }
        });

        buttonFacebook = findViewById(R.id.setting_activity_button_login_facebook);
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.FACEBOOK);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView text = findViewById(R.id.setting_activity_text_info);

        StringBuilder sb = new StringBuilder();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            reload();
            sb.append("DisplayName = " + currentUser.getDisplayName() + "\n") ;
            sb.append("Email = " + currentUser.getEmail()+ "\n");
            sb.append("PhoneNumber = " + currentUser.getPhoneNumber() + "\n");
            sb.append("ProviderId = " + currentUser.getProviderId() + "\n");
            sb.append("TenantId = " + currentUser.getTenantId()+ "\n");
            sb.append("Uid = " + currentUser.getUid() + "\n");
            // pour authentification back-end
            //currentUser.getIdToken()

        } else {
            sb.append("NO USER");
        }

        text.setText(sb.toString());
    }

    private List<AuthUI.IdpConfig> getProviderBySupportedProvider(SupportedProvider supportedProvider){
        switch(supportedProvider){
            case EMAIL:
                return Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
            case GOOGLE:
                return Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
            case FACEBOOK:
                return Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());
            case TWITTER:
                return Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());
            default:
                return null;
        }
    }

    private void startSignInActivity(SupportedProvider supportedProvider){
        List<AuthUI.IdpConfig> providers = getProviderBySupportedProvider(supportedProvider);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void reload(){

    }

    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        FirebaseUiException firebaseUiException = response.getError();

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                }
                else {
                    if (firebaseUiException != null) {
                        if (firebaseUiException.getErrorCode() == ErrorCodes.NO_NETWORK) {
                            showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                        } else if (firebaseUiException.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                            showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                        }
                    }
                }
            }
        }
    }


}