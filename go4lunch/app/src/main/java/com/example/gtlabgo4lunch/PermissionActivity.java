package com.example.gtlabgo4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.navigation.NavigationActivity;

public class PermissionActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback  {

    private static final String TAG = "PermissionActivity";
    final static int PERMISSION_REQUEST_CODE = 101;

    Button buttonGrantPermission;
    Button buttonDiscardPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        if (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            closeActivity();
        }

        buttonGrantPermission = findViewById(R.id.activity_permission_button_grant_permission);
        buttonGrantPermission.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                grantPermission();
            }
        });

        buttonDiscardPermission = findViewById(R.id.activity_permission_button_discard_permission);
        buttonDiscardPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discarsPermission();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void grantPermission(){
        Log.d(TAG, "grantPermission() called");
        String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    private void showGrandedMessage(){
        Toast.makeText(PermissionActivity.this, getString(R.string.permission_granted_message), Toast.LENGTH_SHORT).show();
    }

    private void showDenieddMessage(){
        Toast.makeText(PermissionActivity.this, getString(R.string.permission_denied_message), Toast.LENGTH_SHORT).show();
    }

    private void closeActivity(){
        Log.d(TAG, "closeActivity() called");
        finish();
    }

    private void discarsPermission(){
        Log.d(TAG, "discarsPermission() called");
        showDenieddMessage();
        closeActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult() called");
        Boolean permissionGranted = false;

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && (grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                    permissionGranted = true;
                    break;
                }
            }
        }

        if (permissionGranted) {
            showGrandedMessage();
            Intent intent;
            intent = new Intent(PermissionActivity.this, NavigationActivity.class);
            startActivity(intent);
            closeActivity();
        } else {
            discarsPermission();
        }
    }
}