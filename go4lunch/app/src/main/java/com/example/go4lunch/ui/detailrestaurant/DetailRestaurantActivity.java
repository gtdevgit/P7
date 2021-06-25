package com.example.go4lunch.ui.detailrestaurant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.go4lunch.ui.model.DetailRestaurantViewState;
import com.example.go4lunch.ui.model.SimpleUserViewState;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

    private boolean telephonySupported;

    private String placeId;
    private String uid;
    private String phoneNumber;
    private String website;
    private boolean liked;

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
        uid = currentUser.getUid();

        PackageManager packageManager = getPackageManager();
        telephonySupported = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

        constraintLayout = findViewById(R.id.activity_detail_restaurant_constraint_layout);
        textViewName = findViewById(R.id.activity_detail_restaurant_name);
        textViewInfo = findViewById(R.id.activity_detail_restaurant_info);
        imageView = findViewById(R.id.activity_detail_restaurant_picture);
        imageViewStar1 = findViewById(R.id.activity_detail_restaurant_star1);
        imageViewStar2 = findViewById(R.id.activity_detail_restaurant_star2);
        imageViewStar3 = findViewById(R.id.activity_detail_restaurant_star3);

        configureRecyclerView();
        configureBottonNavigation();
        configureFloatingActionButton();
        configureViewModel();

        loadViewModel(placeId, uid);
    }

    private void configureRecyclerView(){
        recyclerView = findViewById(R.id.activity_detail_restaurant_recyclerview_workmates);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        simpleWorkmatesAdapter = new SimpleWorkmatesAdapter();
        recyclerView.setAdapter(simpleWorkmatesAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setWorkmates(List<SimpleUserViewState> users){
        simpleWorkmatesAdapter.updateData(users);
        simpleWorkmatesAdapter.notifyDataSetChanged();
    }

    private void configureBottonNavigation(){
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

    private void configureFloatingActionButton(){
        floatingActionButton = findViewById(R.id.activity_detail_restaurant_floating_action_button_select);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChoose();
            }
        });
    }

    /**
     * crete viewModel and configure observers
     */
    private void configureViewModel(){
        // todo : DetailRestaurantActivity : use ViewModelProvider to create VM. Create Factory ViewModelFactory to pass arguments.
        detailRestaurantViewModel = new DetailRestaurantViewModel(
                new GooglePlacesApiRepository(getString(R.string.google_api_key)),
                uid,
                placeId);

        detailRestaurantViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showSnackBar(s);
            }
        });

        detailRestaurantViewModel.getDetailRestaurantViewStateLiveData().observe(this, new Observer<DetailRestaurantViewState>() {
            @Override
            public void onChanged(DetailRestaurantViewState detailRestaurantViewState) {
                setRestaurantName(detailRestaurantViewState.getName());
                setInfo(detailRestaurantViewState.getInfo());
                setUrlPicture(detailRestaurantViewState.getUrlPicture());
                setPhoneNumber(detailRestaurantViewState.getPhoneNumber());
                setWebsite(detailRestaurantViewState.getWebsite());
                setStar1Color(detailRestaurantViewState.getStar1Color());
                setStar2Color(detailRestaurantViewState.getStar2Color());
                setStar3Color(detailRestaurantViewState.getStar3Color());
                setWorkmates(detailRestaurantViewState.getWorkmates());
                setChosen(detailRestaurantViewState.isChosenByCurrentUser());
                setLiked(detailRestaurantViewState.isLikedByCurrentUser());
            }
        });

        // liked restaurant observer
        detailRestaurantViewModel.getLikedLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setLiked(aBoolean.booleanValue());
            }
        });

        // workmates who chose this restaurant
        detailRestaurantViewModel.getWorkmatesLiveData().observe(this, new Observer<List<SimpleUserViewState>>() {
            @Override
            public void onChanged(List<SimpleUserViewState> users) {
                setWorkmates(users);
            }
        });

        detailRestaurantViewModel.getStar1ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar1Color(integer.intValue());
            }
        });

        detailRestaurantViewModel.getStar2ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar2Color(integer.intValue());
            }
        });

        detailRestaurantViewModel.getStar3ColorMutableLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setStar3Color(integer.intValue());
            }
        });
    }

    private void loadViewModel(String placeId, String uid){
        detailRestaurantViewModel.load(placeId, uid);
    }

    private void setRestaurantName(String name){
        textViewName.setText(name);
    }

    private void setInfo(String info){
        textViewInfo.setText(info);
    }

    private void setUrlPicture(String urlPicture){
        if (urlPicture == null) {
            Glide.with(imageView.getContext())
                    .load(R.drawable.image_floue)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(urlPicture)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imageView);
        }
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

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        menuItemCall.setVisible((telephonySupported) && (phoneNumber != null));
    }

    private void setWebsite(String website) {
        this.website = website;
        menuItemWebsite.setVisible(website != null);
    }

    private void call(){
        if ((telephonySupported) && (phoneNumber != null)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(String.format("tel:%s", phoneNumber)));
            startActivity(intent);
        }
    }

    private void changeLike(){
        if (liked) {
            // remove like
            Log.d(TAG, "delete like");
            detailRestaurantViewModel.unlike(uid, placeId);
        } else {
            // like
            Log.d(TAG, "create like");
            detailRestaurantViewModel.like(uid, placeId);
        }
    }

    // state liked and UI update
    private void setLiked(boolean isliked){
        Log.d(TAG, "setLiked() called with: isliked = [" + isliked + "]");
        liked = isliked;
        menuItemLike.setChecked(isliked);

        if (isliked){
            menuItemLike.setIcon(R.drawable.ic_baseline_check_24);
        } else {
            menuItemLike.setIcon(R.drawable.ic_baseline_star_24);
        }
    }

    private void changeChoose(){
        detailRestaurantViewModel.changeChose();
    }

    // state chosen and UI update
    private void setChosen(boolean isChosen){
        if (isChosen) {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_star_24);
        }
    }

    private void openWebsite(){
        if ((website != null) && (website.trim().length() > 0)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(website));
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "DetailRestaurantActivity.onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailRestaurantViewModel.activateWormatesByPlaceListener(placeId);
        detailRestaurantViewModel.activateLikedByPlaceListener(placeId);
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
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}