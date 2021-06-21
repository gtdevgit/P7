package com.example.go4lunch.ui.detailrestaurant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.go4lunch.models.viewstate.DetailRestaurantViewState;
import com.example.go4lunch.models.viewstate.SimpleUserViewState;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class DetailRestaurantActivity extends AppCompatActivity {

    // todo : Detail d'un restaurant ajouter toutes les photo. => optionel
    // todo : Detail d'un restaurant. Remplacer le bottom navigation par 3 images

    private static final String TAG = Tag.TAG;
    private String uid;
    private String placeId;
    private String phoneNumber;
    private String website;
    private boolean telephonySupported;
    private boolean liked;
    private boolean chosen;

    private ConstraintLayout constraintLayout;
    private ImageView imageView;
    private ImageView imageViewStar1;
    private ImageView imageViewStar2;
    private ImageView imageViewStar3;
    private TextView textViewName;
    private TextView textViewInfo;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItemCall;
    private MenuItem menuItemLike;
    private MenuItem menuItemWebsite;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SimpleWorkmatesAdapter simpleWorkmatesAdapter;

    private DetailRestaurantViewModel detailRestaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        Bundle bundle = getIntent().getExtras();
        placeId = bundle.getString("placeid", "");
        Log.d(TAG, "DetailRestaurantActivity.onCreate() called with: placeid = [" + placeId + "]");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.uid = currentUser.getUid();

        constraintLayout = findViewById(R.id.activity_detail_restaurant_constraint_layout);
        textViewName = findViewById(R.id.activity_detail_restaurant_name);
        textViewInfo = findViewById(R.id.activity_detail_restaurant_info);
        imageView = findViewById(R.id.activity_detail_restaurant_picture);
        imageViewStar1 = findViewById(R.id.activity_detail_restaurant_star1);
        imageViewStar2 = findViewById(R.id.activity_detail_restaurant_star2);
        imageViewStar3 = findViewById(R.id.activity_detail_restaurant_star3);

        bottomNavigationView = findViewById(R.id.activity_detail_restaurant_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigate(item);
            }
        });
        menuItemCall = bottomNavigationView.getMenu().findItem(R.id.activity_detail_restaurant_menu_item_call);
        menuItemLike = bottomNavigationView.getMenu().findItem(R.id.activity_detail_restaurant_menu_item_like);
        menuItemWebsite = bottomNavigationView.getMenu().findItem(R.id.activity_detail_restaurant_menu_item_website);

        floatingActionButton = findViewById(R.id.activity_detail_restaurant_floating_action_button_select);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChoose();
            }
        });

        this.detailRestaurantViewModel = new DetailRestaurantViewModel(new GooglePlacesApiRepository(getString(R.string.google_api_key)));

        this.detailRestaurantViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showSnackBar(s);
            }
        });

        this.detailRestaurantViewModel.getDetailRestaurantLiveData().observe(this, new Observer<DetailRestaurantViewState>() {
            @Override
            public void onChanged(DetailRestaurantViewState detailRestaurantViewState) {
                textViewName.setText(detailRestaurantViewState.getName());
                setStar1Color(detailRestaurantViewState.getStar1Color());
                setStar2Color(detailRestaurantViewState.getStar2Color());
                setStar3Color(detailRestaurantViewState.getStar3Color());

                textViewInfo.setText(detailRestaurantViewState.getInfo());

                if (detailRestaurantViewState.getUrlPicture() == null) {
                    Glide.with(imageView.getContext())
                            .load(R.drawable.image_floue)
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imageView);
                } else {
                    Glide.with(imageView.getContext())
                            .load(detailRestaurantViewState.getUrlPicture())
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imageView);

                }
                setPhoneNumber(detailRestaurantViewState.getPhoneNumber());
                setWebsite(detailRestaurantViewState.getWebsite());
            }
        });
        this.loadDetailRestaurant(this.placeId);

        // liked restaurant observer
        this.detailRestaurantViewModel.getLikedLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setLiked(aBoolean.booleanValue());
            }
        });
        this.detailRestaurantViewModel.loadIsLiked(uid, this.placeId);

        // chosen observer
        this.detailRestaurantViewModel.getChosenLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setChosen(aBoolean.booleanValue());
            }
        });
        Log.d(TAG, "this.detailRestaurantViewModel.loadIsChosen = [" + uid + "] " + "[" + placeId + "]");
        this.detailRestaurantViewModel.loadIsChosen(uid, placeId);

        PackageManager packageManager = getPackageManager();
        telephonySupported = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

        recyclerView = findViewById(R.id.activity_detail_restaurant_recyclerview_workmates);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        simpleWorkmatesAdapter = new SimpleWorkmatesAdapter();
        recyclerView.setAdapter(simpleWorkmatesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // workmates who chose this restaurant
        this.detailRestaurantViewModel.getWorkmatesLiveData().observe(this, new Observer<List<SimpleUserViewState>>() {
            @Override
            public void onChanged(List<SimpleUserViewState> users) {
                setWorkmates(users);
            }
        });
        this.detailRestaurantViewModel.loadWorkmatesByPlace(this.placeId);

        this.detailRestaurantViewModel.getStar1ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar1Color(integer.intValue());
            }
        });
        this.detailRestaurantViewModel.getStar2ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar2Color(integer.intValue());
            }
        });
        this.detailRestaurantViewModel.getStar3ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar3Color(integer.intValue());
            }
        });
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
                changeLike();
                return true;
            case R.id.activity_detail_restaurant_menu_item_website:
                openWebsite();
        }
        return true;
    }

    private void call(){
        if ((telephonySupported) && (phoneNumber != null)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(String.format("tel:%s", this.phoneNumber)));
            startActivity(intent);
        }
    }

    private void changeLike(){
        if (this.liked) {
            // remove like
            Log.d(TAG, "delete like");
            this.detailRestaurantViewModel.unlike(this.uid, this.placeId);
        } else {
            // like
            Log.d(TAG, "create like");
            this.detailRestaurantViewModel.like(this.uid, this.placeId);
        }
    }

    // state liked and UI update
    private void setLiked(boolean isliked){
        Log.d(TAG, "setLiked() called with: isliked = [" + isliked + "]");
        this.liked = isliked;
        menuItemLike.setChecked(isliked);

        if (this.liked){
            menuItemLike.setIcon(R.drawable.ic_baseline_check_24);
        } else {
            menuItemLike.setIcon(R.drawable.ic_baseline_star_24);
        }
    }

    private void changeChoose(){
        if (this.chosen) {
            //remove choise
            this.detailRestaurantViewModel.unchoose(this.uid, this.placeId);
        } else {
            this.detailRestaurantViewModel.choose(this.uid, this.placeId);
        }
        this.detailRestaurantViewModel.loadWorkmatesByPlace(this.placeId);
    }

    // state chosen and UI update
    private void setChosen(boolean isChosen){
        Log.d(TAG, "setChosen() called with: isChosen = [" + isChosen + "]");
        this.chosen = isChosen;
        if (this.chosen) {
            this.floatingActionButton.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            this.floatingActionButton.setImageResource(R.drawable.ic_baseline_star_24);
        }
    }

    private void openWebsite(){
        if ((website != null) && (website.trim().length() > 0)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(website));
            startActivity(intent);
        }
    }

    private void setWorkmates(List<SimpleUserViewState> users){
        simpleWorkmatesAdapter.updateData(users);
        simpleWorkmatesAdapter.notifyDataSetChanged();
    }

    private void setStar1Color(@ColorRes int color){
        imageViewStar1.setColorFilter(ContextCompat.getColor(this, color));
    }

    private void setStar2Color(@ColorRes int color){
        imageViewStar2.setColorFilter(ContextCompat.getColor(this, color));
    }

    private void setStar3Color(@ColorRes int color){
        imageViewStar3.setColorFilter(ContextCompat.getColor(this, color));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "DetailRestaurantActivity.onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailRestaurantViewModel.activateWormatesByPlaceListener(this.placeId);
        detailRestaurantViewModel.activateLikedByPlaceListener(this.placeId);
        Log.d(TAG, "DetailRestaurantActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "DetailRestaurantActivity.onPause() called");
        detailRestaurantViewModel.removeWormatesByPlaceListener();
        detailRestaurantViewModel.removeLikedByPlaceListener();
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

    private void showSnackBar(String message){
        Snackbar.make(this.constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}