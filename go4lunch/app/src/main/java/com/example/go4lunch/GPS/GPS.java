package com.example.go4lunch.GPS;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class GPS {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private MutableLiveData<Location> liveLocation;

    public GPS(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.liveLocation = new MutableLiveData<Location>();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                liveLocation.setValue(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
    }

    public MutableLiveData<Location> getLivelocation() {
        return liveLocation;
    }

    @SuppressLint("MissingPermission")
    public void startLocalization(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 25, locationListener);
    }

    public void stopLocalization(){
        locationManager.removeUpdates(locationListener);
    }
}
