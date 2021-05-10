package com.example.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.api.googleplaces.GooglePlacesApiClient;
import com.example.go4lunch.api.googleplaces.GooglePlacesApiInterface;
import com.example.go4lunch.models.googleplaces.Textsearch;
import com.google.gson.JsonObject;

import retrofit2.Call;

public class GooglePlacesApiRepository {
    private static final String TAG = "go4lunchdebug";
    
    private GooglePlacesApiInterface api;
    private String apiKey;

    /**
     *
     * @param apiKey
     */
    public GooglePlacesApiRepository(String apiKey) {
        Log.d(TAG, "GooglePlacesApiRepository() called with: apiKey = [" + apiKey + "]");
        this.apiKey = apiKey;
        api = GooglePlacesApiClient.getClient();
    }

    private String getApiKey(){
        return this.apiKey;
    }

    private String getDefaultRadius(){
        return "1000";
    }

    private String getDefaultOpeningHours(){
        return "true";
    }

    /**
     * The Place Autocomplete service is a web service that returns place predictions
     *
     * @param location
     * @return
     */
    public Call<JsonObject> getAutocomplete(Location location) {
        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() strLocation = [" + strLocation + "]");
        try {
            return api.getAutocomplete("restaurant", strLocation,  getDefaultRadius(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() Exception e = [" + e.getMessage() + "]");
            return null;
        }
    }

    public Call<Textsearch> getTextsearch(String query, Location location) {
        Log.d(TAG, "GooglePlacesApiRepository.getTextsearch() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        try {
            return api.getTextsearch(query, strLocation, getDefaultRadius(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getTextsearch() called with: location = [" + location + "]");
            return null;
        }
    }

}
