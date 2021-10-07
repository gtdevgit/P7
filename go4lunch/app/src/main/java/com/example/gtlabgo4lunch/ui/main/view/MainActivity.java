package com.example.gtlabgo4lunch.ui.main.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.ui.main.model.CurrentUser;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModel;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModelFactory;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//implements OnMapReadyCallback
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageView;
    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private Toolbar toolbar;
    private MenuItem menuItemSearch;
    private SearchView searchView;

    private ConstraintLayout constraintLayout;


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

        constraintLayout = findViewById(R.id.activity_main_constraint_layout);

        MainViewModel mainViewModel = new ViewModelProvider(this, MainViewModelFactory.getInstance()).get(MainViewModel.class);
        mainViewModel.getCurrentUserLiveData().observe(this, new Observer<CurrentUser>() {
            @Override
            public void onChanged(CurrentUser currentUser) {
                setUserName(currentUser.getName());
                setUserEmail(currentUser.getEmail());
                setUserPhotoUrl(currentUser.getPhotoUrl());
            }
        });
        mainViewModel.loadCurrentUser();
    }

    private void setUserName(String userName){
        textViewUserName.setText(userName);
    };

    private void setUserEmail(String userEmail){
        textViewUserEmail.setText(userEmail);
    };

    private void setUserPhotoUrl(Uri photoUrl){
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

    public MenuItem getMenuItemSearch(){
        return menuItemSearch;
    }
    public SearchView getSearchView() { return searchView; }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "]");
        getMenuInflater().inflate(R.menu.main, menu);

        menuItemSearch = menu.findItem(R.id.menu_item_toolbar_searchview);
        searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity.onStart() called");
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