package com.example.go4lunch.ui.home;

import android.content.Context;
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
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    private GPS gps;
    private Location location;

    PlacesClient placesClient;
    private MainViewModel mainViewModel;
    private Observer<Autocomplete> autocompleteDataObserver;

    public MapFragment(GPS gps, MainViewModel mainViewModel) {
        // Required empty public constructor
        Log.d(TAG, "MapFragment() called with: gps = [" + gps + "], mainViewModel = [" + mainViewModel + "]");
        this.gps = gps;
        this.mainViewModel = mainViewModel;

        autocompleteDataObserver = new Observer<Autocomplete>() {
            @Override
            public void onChanged(Autocomplete autocomplete) {
                setAutoCompleteData(autocomplete);
            }
        };
        mainViewModel.getAutocompleteData().observe(this, autocompleteDataObserver);
    }

    public void setAutoCompleteData(Autocomplete autocomplete){
        // maj de la carte avec les points de proximités
        Log.d(TAG, "setAutoCompleteData() called with: data = [" + autocomplete.getData() + "]");
    }

    public void setLocation(Location location) {
        Log.d(TAG, "setLocation() called with: location = [" + location + "]");

        this.location = location;

        if(mMap!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latlng).title("You position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
        // demande maj des points de proximités
        mainViewModel.loadAutocompleteData(location);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        floatingActionButton = view.findViewById(R.id.fragment_map_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAroundMe();
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
        mMap = googleMap;
/*        // Add a marker in Tours and move the camera
        LatLng tours = new LatLng(47.3833, 0.6833);
        mMap.addMarker(new MarkerOptions().position(tours).title("Marker in Tours"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tours, 14));*/
        gps.startLocalization();
        if (this.location!=null){
            this.setLocation(this.location);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated() called");

        progressBar = view.findViewById(R.id.fragment_map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // GPS location observer
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

    void findAroundMe() {
        Log.d(TAG, "findAroundMe() called");
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        LatLng origine = new LatLng(location.getLatitude(), location.getLongitude());

        // Create a RectangularBounds object.
        RectangularBounds bounds = GeographicHelper.createRectangularBounds(origine, 500);



        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setOrigin(origine)
                //.setCountries("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                //.setQuery("restaurants")
                .build();

        MyPlace.getInstance().getPlaceClient().findAutocompletePredictions(request)
                .addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
                        Log.d(TAG, "onSuccess() called with: findAutocompletePredictionsResponse = [" + findAutocompletePredictionsResponse + "]");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        Log.d(TAG, "onComplete() called with: task = [" + task + "]");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete() called with: task.isSuccessful = true");
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            Log.d(TAG, "onComplete() called with: predictionsResponse = [" + predictionsResponse + "]");
                            List<AutocompletePrediction> predictionList = predictionsResponse.getAutocompletePredictions();
                            List<String> suggestionsList = new ArrayList<>();
                            for (int i = 0; i < predictionList.size(); i++) {
                                Log.d(TAG, "onComplete() called with: predictionList");
                                AutocompletePrediction prediction = predictionList.get(i);
                                suggestionsList.add(prediction.getFullText(null).toString());
                            }


                        } else {
                            Log.d(TAG, "onComplete() called with: task.isSuccessful = false");
                        }
                    }
                });
    }


}