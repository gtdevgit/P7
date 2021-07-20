package com.example.go4lunch.ui.login;

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

import com.example.go4lunch.ui.main.view.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.firebase.Authentication;
import com.example.go4lunch.firebase.SupportedProvider;
import com.example.go4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    }

    private void configureViewModel(){
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(constraintLayout, s, Snackbar.LENGTH_SHORT).show();
            }
        });
        loginViewModel.getCreatedUserWithSuccessLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean.booleanValue()) {
                    showSnackBar(R.string.connection_succeed);
                }
            }
        });
    }

    private void startSignInActivity(SupportedProvider supportedProvider){
        Log.d(Tag.TAG, "startSignInActivity() called with: supportedProvider = [" + supportedProvider + "]");

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
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d(Tag.TAG, "onFailure() called with: e = [" + e + "]");
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
        loginViewModel.addCurrentUserInFirestore();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
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
}