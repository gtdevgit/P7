package com.example.go4lunch.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.go4lunch.GPS.GPS;
import com.example.go4lunch.MyPlace.GeographicHelper;
import com.example.go4lunch.MyPlace.MyPlace;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = Tag.TAG;

    private GoogleMap mMap;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private Location location;
    private List<Restaurant> restaurants;

    private MainViewModel mainViewModel;

    public MapFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        Log.d(TAG, "MapFragment() called with: mainViewModel = [" + mainViewModel + "]");
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "MapFragment.onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // GPS location observer
        this.mainViewModel.getLocationMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "MapFragment.onChanged(location) called with: location = [" + location + "]");
                setLocation(location);
            }
        });

        mainViewModel.getListRestaurant().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(TAG, "MapFragment.onChanged(restaurants) called with: restaurants = [" + restaurants + "]");
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
        Log.d(TAG, "MapFragment.onMapReady() called with: googleMap = [" + googleMap + "]");
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
        Log.d(TAG, "MapFragment.onViewCreated() called");

        progressBar = view.findViewById(R.id.fragment_map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setLocation(Location location) {
        Log.d(TAG, "MapFragment.setLocation() called with: mMap = [" + mMap + "]. location = [" + location + "]");
        progressBar.setVisibility(View.VISIBLE);
        this.location = location;
        if(mMap!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latlng).title("You position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
        //mainViewModel.setLocation(location);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setRestaurants(List<Restaurant> restaurants){
        Log.d(TAG, "MapFragment.setRestaurants() called with: mMap = [" + mMap + "]. restaurants = [" + restaurants + "]");
        this.restaurants = restaurants;
        if(mMap!=null) {
            progressBar.setVisibility(View.VISIBLE);
            for (Restaurant restaurant : restaurants){
//                Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_restaurant_24);
//                BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);

                LatLng latlng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(restaurant.getName())
//                        .icon(icon)
                );
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
    public void onPause() {
        Log.d(TAG, "MapFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MapFragment.onResume() called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MapFragment.onStart() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}