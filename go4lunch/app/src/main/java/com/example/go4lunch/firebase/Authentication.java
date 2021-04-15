package com.example.go4lunch.firebase;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Authentication {

    public static boolean isConnected(){
        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

    public static List<AuthUI.IdpConfig> getProviderBySupportedProvider(SupportedProvider supportedProvider){
        switch(supportedProvider){
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
}
