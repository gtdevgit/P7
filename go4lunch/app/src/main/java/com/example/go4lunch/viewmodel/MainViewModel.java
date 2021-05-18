package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.GPS.GPS;
import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.googleplaces.Result;
import com.example.go4lunch.models.googleplaces.Textsearch;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private static final String TAG = "go4lunchdebug";

    List<Restaurant> restaurantsCache;

    /**
     * GooglePlacesApiRepository
     */
    private GooglePlacesApiRepository googlePlacesApiRepository;
    private final MutableLiveData<Autocomplete> autocompleteMutableLiveData = new MutableLiveData<Autocomplete>();
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = new MutableLiveData<List<Restaurant>>();
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<Location>();

    public MainViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        Log.d(TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        restaurantsCache = new ArrayList<Restaurant>();
    }

    /**
     * autocompleteMutableLiveData property is exposed as livedata to prevent external modifications
     * @return
     */
    public LiveData<Autocomplete> getAutocomplete(){ return autocompleteMutableLiveData; }

    /**
     * errorMutableLiveData property is exposed as livedata to prevent external modifications
     * @return
     */
    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    /**
     * locationMutableLiveData property is exposed as livedata to prevent external modifications
     * @return
     */
    public LiveData<List<Restaurant>> getRestaurantsLiveData() {return  this.restaurantsMutableLiveData; }

    /**
     * locationMutableLiveData property is exposed as livedata to prevent external modifications
     * @return
     */
    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }

    /**
     * setLocation notify the view model that the location has changed
     * @param location
     */
    public void setLocation(Location location){
        Log.d(TAG, "MainViewModel.setLocation() called with: location = [" + location + "]");
        this.locationMutableLiveData.postValue(location);
        Log.d(TAG, "MainViewModel.setLocation() 2 called with: location = [" + location + "]");
        this.loadRestaurantAround(location);
        Log.d(TAG, "MainViewModel.setLocation() 3 called with: location = [" + location + "]");
    }

    /**
     * this data is kept in cache
     */
    public List<Restaurant> getRestaurantsCache() {
        return restaurantsCache;
    }

    /**
     * loadRestaurantAround
     * @param location
     */
    private void loadRestaurantAround(Location location){
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: location = [" + location + "]");
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        //Call<Textsearch> call = googlePlacesApiRepository.getTextsearch("restaurant", location);
        Call<Textsearch> call = googlePlacesApiRepository.getNearbysearch(location);
        Log.d(TAG, "loadRestaurantAround() 2 called with: location = [" + location + "]");
        call.enqueue(new Callback<Textsearch>() {
            @Override
            public void onResponse(Call<Textsearch> call, Response<Textsearch> response) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()){
                    Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() isSuccessful=true");
                    Textsearch textsearch = response.body();
                    List<Result> lstResult = textsearch.getResults();
                    List<Restaurant> restaurants = new ArrayList<Restaurant>();
                    for (Result result : lstResult) {
                        String name = result.getName();
                        //Log.d(TAG, "name=" + name);
                        double latitude = result.getGeometry().getLocation().getLat();
                        //Log.d(TAG, "latitude=" + latitude);
                        double longitude = result.getGeometry().getLocation().getLng();
                        Restaurant restaurant = new Restaurant(name, latitude, longitude);

                        // compatibility with getTextsearch and getNearbysearch
                        // extract address from getTextsearch (formatedAddress) or getNearbysearch (vivinity)
                        String address = "";
                        if (result.getFormattedAddress() != null) {
                            address = result.getFormattedAddress();
                        } else {
                            if (result.getVicinity() != null)
                                address = result.getVicinity();
                        }
                        if ((address != null) && (address.trim().length() > 0) && (address.indexOf(',') >= 0)) {
                            address = address.split(",")[0];
                        }
                        restaurant.setInfo(address);

                        //restaurant.setHours(result.getPriceLevel().toString());
                        if ((result.getPhotos() != null) && (result.getPhotos().size() >= 1)) {
                            Log.d(TAG, "getPhotoReference() = " + result.getPhotos().get(0).getPhotoReference());
                            restaurant.setUrlPicture(googlePlacesApiRepository.getUrlPlacePhoto(result.getPhotos().get(0).getPhotoReference()));
                        } else {
                            if (result.getIcon() != null) {
                                Log.d(TAG, "getIcon() = " + result.getIcon());
                                restaurant.setUrlPicture(result.getIcon());
                            }
                        }

                        restaurants.add(restaurant);
                    }
                    // Memorize last restaurants
                    restaurantsCache.clear();
                    restaurantsCache.addAll(restaurants);
                    // live data
                    restaurantsMutableLiveData.postValue(restaurants);
                } else {
                    Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() isSuccessful=false");
                }
            }

            @Override
            public void onFailure(Call<Textsearch> call, Throwable t) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
                errorMutableLiveData.postValue(t.getMessage());
            }
        });

    }
}
