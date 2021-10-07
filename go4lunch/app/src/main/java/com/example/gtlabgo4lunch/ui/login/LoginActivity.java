package com.example.gtlabgo4lunch.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.gtlabgo4lunch.ui.main.view.MainActivity;
import com.example.go4lunch.R;
import com.example.gtlabgo4lunch.firebase.Authentication;
import com.example.gtlabgo4lunch.firebase.SupportedProvider;
import com.example.gtlabgo4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    private Button buttonFacebook;
    private Button buttonTwitter;
    private SignInButton buttonGoogle;

    private ConstraintLayout constraintLayout;

    private LoginViewModel loginViewModel;

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

        buttonTwitter = findViewById(R.id.activity_login_button_login_twitter);
        buttonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.TWITTER);
            }
        });

        buttonGoogle = findViewById(R.id.activity_login_button_login_google);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity(SupportedProvider.GOOGLE);
            }
        });

        configureViewModel();
        enabledUI();
    }

    private void configureViewModel(){
        loginViewModel = new ViewModelProvider(this, LoginViewModelFactory.getInstance()).get(LoginViewModel.class);

        loginViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(constraintLayout, s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void startSignInActivity(SupportedProvider supportedProvider){
        Log.d(Tag.TAG, "startSignInActivity() called with: supportedProvider = [" + supportedProvider + "]");
        disableUI();
        if (supportedProvider == SupportedProvider.TWITTER) {
            startSignInWithTwitter();
        }
        else
            {
            List<AuthUI.IdpConfig> providers = Authentication.getProviderBySupportedProvider(supportedProvider);

            startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Tag.TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.d(Tag.TAG, "onActivityResult. pendingResultTask. onSuccess() called with: authResult = [" + authResult + "]");
                                    createUserInFirestore();
                                    // go to main after login
                                    if (Authentication.isConnected()) {
                                        Intent intent;
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        enabledUI();
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d(Tag.TAG, "onFailure() called with: e = [" + e + "]");
                                    enabledUI();
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            startSignInWithTwitter();
        }

        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void createUserInFirestore(){
        Log.d(Tag.TAG, "createUserInFirestore() called");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){

            String uid = currentUser.getUid();
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String urlPicture = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;

            loginViewModel.addCurrentUserInFirestore(uid, userName, userEmail, urlPicture);
        }
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        Log.d(Tag.TAG, "handleResponseAfterSignIn() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();

                // go to main after login
                if (Authentication.isConnected()) {
                    Intent intent;
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    enabledUI();
                }
            } else {
                // ERRORS
                enabledUI();
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

    private void startSignInWithTwitter(){
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        String language = Locale.getDefault().getLanguage();
        // Target specific email with login hint.
        provider.addCustomParameter("lang", language);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(Tag.TAG, "startSignInWithTwitter. onSuccess()");
                                showSnackBar(R.string.connection_succeed);
                                // User is signed in.
                                createUserInFirestore();
                                // go to main after login
                                if (Authentication.isConnected()) {
                                    Intent intent;
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                                Snackbar.make(constraintLayout, "startSignInWithTwitter " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
    }

    private void enabledUI(){
        buttonFacebook.setEnabled(true);
        buttonGoogle.setEnabled(true);
        buttonTwitter.setEnabled(true);
    }

    private void disableUI(){
        buttonFacebook.setEnabled(false);
        buttonGoogle.setEnabled(false);
        buttonTwitter.setEnabled(false);
    }
}