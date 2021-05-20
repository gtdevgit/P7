package com.example.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.MyPlace.MyPlace;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.ui.SettingActivity;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Observer;

//implements OnMapReadyCallback
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageView;
    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private Toolbar toolbar;
    private MenuItem menuItemSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_drawer_home, R.id.navigation_drawer_setting, R.id.navigation_drawer_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // header component's
        if (navigationView.getHeaderCount() >= 1) {
            View headView = navigationView.getHeaderView(0);
            imageView = headView.findViewById(R.id.navigation_header_imageView_user);
            textViewUserName = headView.findViewById(R.id.navigation_header_user_name);
            textViewUserEmail = headView.findViewById(R.id.navigation_header_user_email);
        }
    }

    public MenuItem getMenuItemSearch(){
        return menuItemSearch;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.main, menu);
        menuItemSearch = toolbar.getMenu().findItem(R.id.menu_item_toolbar_searchview);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setting(){

            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

            //Intent intent = new Intent(MainActivity.this, AddActivity.class);
            //startActivityForResult(intent, REQUESTCODE_START_ACTIVITY_ADD_ACTIVITY);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                setting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity.onStart() called");

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {

            if ((imageView != null) && (currentUser.getPhotoUrl() != null)) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }

            //Get email & username from Firebase
            if (textViewUserName != null) {
                String username = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.no_user_name_found) : currentUser.getDisplayName();
                textViewUserName.setText(username);
            }

            if (textViewUserEmail != null) {
                String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.no_user_email) : currentUser.getEmail();
                textViewUserEmail.setText(email);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivityonResume() called");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "MainActivity.onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "MainActivity.onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity.onDestroy() called");
        super.onDestroy();
    }
}