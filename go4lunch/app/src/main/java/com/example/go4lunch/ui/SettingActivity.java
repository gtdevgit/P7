package com.example.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        button = findViewById(R.id.setting_activity_button_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity();
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
        } else {
            sb.append("NO USER");
        }

        text.setText(sb.toString());
    }

    private void startSignInActivity(){

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        //        new AuthUI.IdpConfig.PhoneBuilder().build(),
        //        new AuthUI.IdpConfig.GoogleBuilder().build(),
        //        new AuthUI.IdpConfig.FacebookBuilder().build(),
        //        new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers) //EMAIL
                        .build(),
                RC_SIGN_IN);
    }

    private void reload(){

    }
}