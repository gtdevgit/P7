package com.example.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.api.googleplaces.GooglePlacesApiClient;
import com.example.go4lunch.api.googleplaces.GooglePlacesApiInterface;
import com.google.gson.JsonObject;

import retrofit2.Call;

public class GooglePlacesApiRepository {
    private static final String TAG = "go4lunchdebug";
    
    private GooglePlacesApiInterface api;
    private String apiKey;

    public GooglePlacesApiRepository(String apiKey) {
        Log.d(TAG, "GooglePlacesApiRepository() called with: apiKey = [" + apiKey + "]");
        this.apiKey = apiKey;
        api = GooglePlacesApiClient.getClient();
    }

    private String getApiKey(){
        return this.apiKey;
    }

    public Call<JsonObject> getAutocomplete(Location location) {
        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() strLocation = [" + strLocation + "]");
        try {
            return api.getAutocomplete("restaurant", strLocation,  "100", getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() Exception e = [" + e.getMessage() + "]");
            return null;
        }
    }
}
