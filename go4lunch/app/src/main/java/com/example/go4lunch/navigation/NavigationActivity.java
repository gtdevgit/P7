package com.example.go4lunch.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.go4lunch.ui.main.view.MainActivity;
import com.example.go4lunch.PermissionActivity;
import com.example.go4lunch.firebase.Authentication;
import com.example.go4lunch.ui.login.LoginActivity;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        // if not permission ask for permission
        if (ContextCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            intent = new Intent(NavigationActivity.this, PermissionActivity.class);
            startActivity(intent);
        }

        // if permission ok check how to open application
        if (ContextCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Authentication.isConnected()) {
                // open main
                intent = new Intent(NavigationActivity.this, MainActivity.class);
            } else {
                // ask for user authentication
                intent = new Intent(NavigationActivity.this,
                        LoginActivity.class);
            }
            startActivity(intent);
        }

        finish();
    }
}