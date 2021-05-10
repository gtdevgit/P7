package com.example.go4lunch.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = Tag.TAG;
    private static final int RC_SIGN_IN = 1;

    private Button buttonFacebook;
    private SignInButton buttonGoogle;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        constraintLayout = findViewById(R.id.activity_login_constraint_layout);

        buttonFacebook = findViewById(R.id.activity_login_button_login_facebook);
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.FACEBOOK);
            }
        });

        buttonGoogle = findViewById(R.id.activity_login_button_login_google);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.GOOGLE);
            }
        });
    }

    private void startSignInActivity(SupportedProvider supportedProvider){
        Log.d(TAG, "startSignInActivity() called with: supportedProvider = [" + supportedProvider + "]");
        List<AuthUI.IdpConfig> providers = Authentication.getProviderBySupportedProvider(supportedProvider);

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

    private void createUserInFirestore(){
        Log.d(TAG, "createUserInFirestore() called");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){

            String urlPicture = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String uid = currentUser.getUid();

            UserHelper.createUser(uid, userName, userEmail, urlPicture)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                        showSnackBar(R.string.error_during_connection);
                    }
                });
        }
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "handleResponseAfterSignIn() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Log.d(TAG, "handleResponseAfterSignIn() response = [" + response + "]");

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "handleResponseAfterSignIn() currentUser = [" + currentUser + "]");
                Log.d(TAG, "handleResponseAfterSignIn() currentUser.getEmail() = [" + currentUser.getEmail() + "]");
                Log.d(TAG, "handleResponseAfterSignIn() currentUser.getUid() = [" + currentUser.getUid() + "]");

                showSnackBar(R.string.connection_succeed);

                // go to main after login
                if (Authentication.isConnected()) {
                    Intent intent;
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(R.string.error_authentication_canceled);
                }
                else {
                    FirebaseUiException firebaseUiException = response.getError();
                    if (firebaseUiException != null) {
                        if (firebaseUiException.getErrorCode() == ErrorCodes.NO_NETWORK) {
                            showSnackBar(R.string.error_no_internet);
                        } else if (firebaseUiException.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                            showSnackBar(R.string.error_unknown_error);
                        } else {
                            showSnackBar(R.string.other_exception);
                        }
                    }
                    else {
                        showSnackBar(R.string.no_exception);
                    }
                }
            }
        }
    }

    private void showSnackBar(int resId){
        Snackbar.make(this.constraintLayout, getString(resId), Snackbar.LENGTH_SHORT).show();
    }

}