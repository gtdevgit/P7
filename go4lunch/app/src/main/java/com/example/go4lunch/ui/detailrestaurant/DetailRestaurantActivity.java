package com.example.go4lunch.ui.detailrestaurant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.models.DetailRestaurant;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;

import static android.widget.Toast.LENGTH_SHORT;

public class DetailRestaurantActivity extends AppCompatActivity {

    private static final String TAG = Tag.TAG;
    private String name;
    private String placeId;
    private String phoneNumber;
    private String website;
    private boolean telephonySupported;

    private ImageView imageView;
    private TextView textViewName;
    private TextView textViewInfo;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItemCall;
    private MenuItem menuItemWebsite;

    private DetailRestaurantViewModel detailRestaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name", "");
        placeId = bundle.getString("placeid", "");

        Log.d(TAG, "DetailRestaurantActivity.onCreate() called with: name = [" + name + "]");
        Log.d(TAG, "DetailRestaurantActivity.onCreate() called with: placeid = [" + placeId + "]");

        textViewName = findViewById(R.id.activity_detail_restaurant_name);
        textViewInfo = findViewById(R.id.activity_detail_restaurant_info);
        imageView = findViewById(R.id.activity_detail_restaurant_picture);

        bottomNavigationView = findViewById(R.id.activity_detail_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigate(item);
            }
        });
        menuItemCall = bottomNavigationView.getMenu().findItem(R.id.activity_detail_restaurant_menu_item_call);
        menuItemWebsite = bottomNavigationView.getMenu().findItem(R.id.activity_detail_restaurant_menu_item_website);

        this.detailRestaurantViewModel = new DetailRestaurantViewModel(new GooglePlacesApiRepository(getString(R.string.google_api_key)));
        this.detailRestaurantViewModel.getDetailRestaurantMutableLiveData().observe(this, new Observer<DetailRestaurant>() {
            @Override
            public void onChanged(DetailRestaurant detailRestaurant) {
                textViewName.setText(detailRestaurant.getName());
                textViewInfo.setText(detailRestaurant.getInfo());

                if (detailRestaurant.getUrlPicture() != null) {
                    Glide.with(imageView.getContext())
                            .load(detailRestaurant.getUrlPicture())
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imageView);
                }
                setPhoneNumber(detailRestaurant.getPhoneNumber());
                setWebsite(detailRestaurant.getWebsite());
            }
        });
        this.loadDetailRestaurant(this.placeId);

        PackageManager packageManager = getPackageManager();
        telephonySupported = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    private void loadDetailRestaurant(String placeId){
        Log.d(TAG, "loadDetailRestaurant() called with: placeId = [" + placeId + "]");
        detailRestaurantViewModel.loadDetailRestaurant(placeId);
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        menuItemCall.setVisible((telephonySupported) && (phoneNumber != null));
    }

    private void setWebsite(String website) {
        this.website = website;
        menuItemWebsite.setVisible(website != null);
    }

    private boolean navigate(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_detail_restaurant_menu_item_call:
                call();
            case R.id.activity_detail_restaurant_menu_item_like:
                Toast.makeText(this, "like", LENGTH_SHORT).show();
                return true;
            case R.id.activity_detail_restaurant_menu_item_website:
                openWebsite();
        }
        return false;
    }

    private void call(){
        if ((telephonySupported) && (phoneNumber != null)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(String.format("tel:%s", this.phoneNumber)));
            startActivity(intent);
        }
    }

    private void openWebsite(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(website));
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "DetailRestaurantActivity.onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DetailRestaurantActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "DetailRestaurantActivity.onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "DetailRestaurantActivity.onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DetailRestaurantActivity.onDestroy() called");
        super.onDestroy();
    }
}