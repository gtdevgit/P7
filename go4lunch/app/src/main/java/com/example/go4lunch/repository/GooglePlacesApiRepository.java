package com.example.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.api.googleplaces.GooglePlacesApiClient;
import com.example.go4lunch.api.googleplaces.GooglePlacesApiInterface;
import com.example.go4lunch.models.googleplaces.palcesdetails.PlaceDetails;
import com.example.go4lunch.models.googleplaces.placesearch.PlaceSearch;
import com.example.go4lunch.tag.Tag;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository giving access to the web services Google Places API
 */
public class GooglePlacesApiRepository {
    private static final String TAG = "go4lunchdebug";

    private GooglePlacesApiInterface api;
    private String apiKey;

    /**
     * @param apiKey
     */
    public GooglePlacesApiRepository(String apiKey) {
        Log.d(TAG, "GooglePlacesApiRepository() called with: apiKey = [" + apiKey + "]");
        this.apiKey = apiKey;
        this.api = GooglePlacesApiClient.getClient();
    }

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    public LiveData<String> getErrorLiveData() { return errorMutableLiveData; }

    private String getApiKey() {
        return this.apiKey;
    }

    private String getDefaultRadius() {
        return "1000";
    }

    private String getDefaultOpeningHours() {
        return "true";
    }

    /**
     * see https://developers.google.com/maps/documentation/places/web-service/supported_types
     *
     * @return
     */
    private String getDefaultType() {
        return "restaurant";
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
            return api.getAutocomplete("restaurant", strLocation, getDefaultRadius(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    public Call<PlaceSearch> getTextsearch(String query, Location location) {
        Log.d(TAG, "GooglePlacesApiRepository.getTextsearch() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        try {
            return api.getTextsearch(query, strLocation, getDefaultRadius(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getTextsearch(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    private MutableLiveData<PlaceDetails> placeDetailsMutableLiveData = new MutableLiveData<>();
    public LiveData<PlaceDetails> getPlaceDetailsLiveData() {
        return placeDetailsMutableLiveData;
    }

    public Call<PlaceDetails> getDetails(String placeId) {
        Log.d(TAG, "getDetails() called with: placeId = [" + placeId + "]");
        try {
            return api.getDetails(placeId, getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getDetails(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    public void loadDetails(String placeId) {
        Call<PlaceDetails> call = getDetails(placeId);
        call.enqueue(new Callback<PlaceDetails>(){
            @Override
            public void onResponse (Call < PlaceDetails > call, Response< PlaceDetails > response){
                if (response.isSuccessful()) {
                    PlaceDetails placeDetails = response.body();
                    placeDetailsMutableLiveData.setValue(placeDetails);
                }
            }
            @Override
            public void onFailure (Call < PlaceDetails > call, Throwable t){
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
    }

    public Call<PlaceSearch> getNearbysearch(Location location){
        Log.d(TAG, "GooglePlacesApiRepository.getNearbysearch() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        try {
            return api.getNearbysearch(strLocation, getDefaultRadius(), getDefaultType(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getNearbysearch(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    /**
     * Google place photos service
     * https://developers.google.com/maps/documentation/places/web-service/photos
     * @param photoreference
     * @return
     */
    public String getUrlPlacePhoto(String photoreference) {
        final String url = "https://maps.googleapis.com/maps/api/place/photo";
        final int size = 200;
        return  String.format("%s?maxwidth=%d&photoreference=%s&key=%s", url, size, photoreference, getApiKey());
    }

}
