package com.example.go4lunch.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.go4lunch.R;
import com.example.go4lunch.models.viewstate.Restaurant;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.detailrestaurant.DetailRestaurantActivity;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private Location location;
    private List<Restaurant> restaurants;

    private MainViewModel mainViewModel;

    public MapFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        Log.d(Tag.TAG, "MapFragment()");
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Tag.TAG, "MapFragment.onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        floatingActionButton = view.findViewById(R.id.fragment_map_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerUserPosition();
            }
        });

        // location observer
        this.mainViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(Tag.TAG, "MapFragment.onChanged(location)");
                setLocation(location);
            }
        });

        // restaurants observer
        mainViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(Tag.TAG, "MapFragment.onChanged(restaurants)");
                setRestaurants(restaurants);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(Tag.TAG, "MapFragment.onMapReady(). (this.location==null) = " + (this.location==null));
        mMap = googleMap;
        // cycle de vie. Navigation list->map
        if (this.location != null) {
            this.setLocation(this.location);
        }
        if (this.restaurants != null) {
            this.setRestaurants(restaurants);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Tag.TAG, "MapFragment.onViewCreated() called");
        progressBar = view.findViewById(R.id.fragment_map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setLocation(Location location) {
        try{
        Log.d(Tag.TAG, "MapFragment.setLocation(location)");
        progressBar.setVisibility(View.VISIBLE);
        this.location = location;
        if(mMap!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latlng).title("You position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
        //mainViewModel.setLocation(location);
        progressBar.setVisibility(View.INVISIBLE);
        }
        catch (Exception e) {
            Log.d(Tag.TAG, "setLocation() called with: Exception = [" + e.getMessage() + "]");
        }
    }

    private void openDetailRestaurantActivity(String placeId){
        Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("placeid", placeId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String placeId = (String) marker.getTag();
        openDetailRestaurantActivity(placeId);
        return false;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setRestaurants(List<Restaurant> restaurants){
        Log.d(Tag.TAG, "MapFragment.setRestaurants(restaurants) (mMap==null) = " + (mMap==null));
        this.restaurants = restaurants;
        if(mMap!=null) {
            progressBar.setVisibility(View.VISIBLE);
            // todo : this Bitmap must be in ressources
            Bitmap bitmapUnselected = drawableToBitmap(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24_primary_color, getContext().getTheme()));
            Bitmap bitmapSelected = drawableToBitmap(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24_selected, getContext().getTheme()));
            for (Restaurant restaurant : restaurants){
                LatLng latlng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap((restaurant.getWorkmatesCount() > 0 ? bitmapSelected : bitmapUnselected)))
                );
                marker.setTag(restaurant.getPlaceId());
                mMap.setOnMarkerClickListener(this::onMarkerClick);
            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Tag.TAG, "MapFragment.onStart() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "MapFragment.onResume()");
        if ((mMap != null) && (this.location != null)) {
            this.mainViewModel.activateChosenRestaurantListener();
        }
    }

    @Override
    public void onPause() {
        Log.d(Tag.TAG, "MapFragment.onPause() called");
        this.mainViewModel.removerChosenRestaurantListener();
        super.onPause();
    }

    private void centerUserPosition(){
        if ((mMap != null) && (this.location != null)) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
    }
}