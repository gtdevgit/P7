package com.example.go4lunch.ui.home;

import android.content.Context;
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

import com.example.go4lunch.GPS.GPS;
import com.example.go4lunch.R;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = Tag.TAG;

    private GoogleMap mMap;
    private ProgressBar progressBar;

    private GPS gps;
    private Location location;

    public MapFragment(GPS gps) {
        // Required empty public constructor
        this.gps = gps;
        Log.d(TAG, "MapFragment() called with: gps = [" + gps + "]");
    }

    public void setLocation(Location location) {
        Log.d(TAG, "setLocation() called with: location = [" + location + "]");
        progressBar.setVisibility(View.INVISIBLE);

        this.location = location;

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlng).title("You position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/*        // Add a marker in Tours and move the camera
        LatLng tours = new LatLng(47.3833, 0.6833);
        mMap.addMarker(new MarkerOptions().position(tours).title("Marker in Tours"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tours, 14));*/
        gps.startLocalization();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated() called");

        progressBar = view.findViewById(R.id.fragment_map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // GPS
        try {
            gps.getLivelocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    setLocation(location);
                }
            });
        }
        catch (Exception e) {
            Log.d(TAG, "gps exception = [" + e.getMessage() + "]");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}