package com.example.go4lunch.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.firebase.Authentication;
import com.example.go4lunch.firebase.LoginActivity;
import com.example.go4lunch.ui.SettingActivity;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        if (Authentication.isConnected()) {
            intent = new Intent(NavigationActivity.this, MainActivity.class);
        } else {
            intent = new Intent(NavigationActivity.this,
                    LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}