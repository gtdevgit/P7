package com.example.go4lunch.data.googleplace.repository;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data.googleplace.api.GooglePlacesApiClient;
import com.example.go4lunch.data.googleplace.api.GooglePlacesApiInterface;
import com.example.go4lunch.data.googleplace.model.autocomplete.Autocomplete;
import com.example.go4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.google.gson.JsonObject;

import java.util.List;

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
    public Call<Autocomplete> getAutocomplete(Location location, String query) {
        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() called with: location = [" + location + "]");
        String strLocation = "" + location.getLatitude() + "," + location.getLongitude();
        //String inputQuery = String.format("restaurant+%s", query);
        String inputQuery = query;

        Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete() strLocation = [" + strLocation + "]");
        try {
            return api.getAutocomplete(inputQuery, "establishment", strLocation, getDefaultRadius(), getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getAutocomplete(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    private MutableLiveData<Autocomplete> autocompleteMutableLiveData = new MutableLiveData<>();
    public LiveData<Autocomplete> getAutocompleteLiveData() { return autocompleteMutableLiveData; }

    public void loadAutocomplete(Location location, String query){
        Call<Autocomplete> call = getAutocomplete(location, query);
        call.enqueue(new Callback<Autocomplete>() {
            @Override
            public void onResponse(Call<Autocomplete> call, Response<Autocomplete> response) {
                if (response.isSuccessful()) {
                    Autocomplete autocomplete = response.body();
                    autocompleteMutableLiveData.setValue(autocomplete);
                }
            }
            @Override
            public void onFailure(Call<Autocomplete> call, Throwable t) {
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
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

    public Call<PlaceDetails> getDetails(String placeId) {
        Log.d(TAG, "getDetails() called with: placeId = [" + placeId + "]");
        try {
            return api.getDetails(placeId, getApiKey());
        } catch (Exception e) {
            Log.d(TAG, "GooglePlacesApiRepository.getDetails(). Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    private MutableLiveData<PlaceDetails> placeDetailsMutableLiveData = new MutableLiveData<>();
    public LiveData<PlaceDetails> getPlaceDetailsLiveData() {
        return placeDetailsMutableLiveData;
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

    public LiveData<PlaceDetails> getPaceDetails(String placeId){
        MutableLiveData<PlaceDetails> resultMutableLiveData = new MutableLiveData<>();
        Call<PlaceDetails> call = getDetails(placeId);
        call.enqueue(new Callback<PlaceDetails>(){
            @Override
            public void onResponse (Call < PlaceDetails > call, Response< PlaceDetails > response){
                if (response.isSuccessful()) {
                    PlaceDetails placeDetails = response.body();
                    resultMutableLiveData.setValue(placeDetails);
                }
            }
            @Override
            public void onFailure (Call < PlaceDetails > call, Throwable t){
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
        return resultMutableLiveData;
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

    private MutableLiveData<PlaceSearch> nearbysearchMutableLiveData = new MutableLiveData<>();
    public LiveData<PlaceSearch> getNearbysearchLiveData() {
        return nearbysearchMutableLiveData;
    }

    public void loadNearbysearch(Location location) {
        Call<PlaceSearch> call = getNearbysearch(location);
        call.enqueue(new Callback<PlaceSearch>() {
            @Override
            public void onResponse(Call<PlaceSearch> call, Response<PlaceSearch> response) {
                if (response.isSuccessful()) {
                    PlaceSearch placeSearch = response.body();
                    nearbysearchMutableLiveData.setValue(placeSearch);
                }
            }

            @Override
            public void onFailure(Call<PlaceSearch> call, Throwable t) {
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
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
