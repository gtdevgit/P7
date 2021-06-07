package com.example.go4lunch.GPS;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.tag.Tag;

public class GPS {

    private static final String TAG = Tag.TAG;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    public GPS(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "GPS.onLocationChanged(location) lat=" + location.getLatitude() + ", long=" + location.getLongitude());
                locationMutableLiveData.setValue(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
    }

    public MutableLiveData<Location> getLocationMutableLiveData() {
        return locationMutableLiveData;
    }

    @SuppressLint("MissingPermission")
    public void startLocalization(){
        Log.d(TAG, "GPS.startLocalization() called");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 25, locationListener);
    }

    public void stopLocalization(){
        Log.d(TAG, "GPS.stopLocalization() called");
        locationManager.removeUpdates(locationListener);
    }
}
