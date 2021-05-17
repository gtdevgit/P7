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

    List<Restaurant> lastRestaurants;

    private GooglePlacesApiRepository googlePlacesApiRepository;
    private final MutableLiveData<Autocomplete> autocompleteData = new MutableLiveData<Autocomplete>();
    private final MutableLiveData<String> error = new MutableLiveData<String>();
    private final MutableLiveData<List<Restaurant>> listRestaurant = new MutableLiveData<List<Restaurant>>();
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<Location>();

    public MainViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        Log.d(TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        lastRestaurants = new ArrayList<Restaurant>();
    }

    public MutableLiveData<Autocomplete> getAutocompleteData(){ return autocompleteData; }
    public MutableLiveData<String> getError(){
        return this.error;
    }
    public MutableLiveData<List<Restaurant>> getListRestaurant() {return  this.listRestaurant; }

    public MutableLiveData<Location> getLocationMutableLiveData() {
        return locationMutableLiveData;
    }

    public void setLocation(Location location){
        Log.d(TAG, "MainViewModel.setLocation() called with: location = [" + location + "]");
        this.locationMutableLiveData.postValue(location);
        Log.d(TAG, "MainViewModel.setLocation() 2 called with: location = [" + location + "]");
        this.loadRestaurantAround(location);
        Log.d(TAG, "MainViewModel.setLocation() 3 called with: location = [" + location + "]");
    }

    public List<Restaurant> getLastRestaurants() {
        return lastRestaurants;
    }

    /**
     * loadRestaurantAround
     * @param location
     */
    private void loadRestaurantAround(Location location){
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: location = [" + location + "]");
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        Call<Textsearch> call = googlePlacesApiRepository.getTextsearch("restaurant", location);
        Log.d(TAG, "loadRestaurantAround() 2 called with: location = [" + location + "]");
        call.enqueue(new Callback<Textsearch>() {
            @Override
            public void onResponse(Call<Textsearch> call, Response<Textsearch> response) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()){
                    Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() isSuccessful=true");
                    Textsearch textsearch = response.body();
                    Log.d(TAG, "textsearch = [" + textsearch + "]");
                    List<Result> lstResult = textsearch.getResults();
                    List<Restaurant> restaurants = new ArrayList<Restaurant>();
                    for (Result result : lstResult) {
                        String name = result.getName();
                        //Log.d(TAG, "name=" + name);
                        double latitude = result.getGeometry().getLocation().getLat();
                        //Log.d(TAG, "latitude=" + latitude);
                        double longitude = result.getGeometry().getLocation().getLng();
                        Restaurant restaurant = new Restaurant(name, latitude, longitude);
                        String formatedAddress = result.getFormattedAddress();
                        if ((formatedAddress != null) &&
                                (formatedAddress.length() > 0) &&
                                (formatedAddress.indexOf(',') >= 0)) {
                            formatedAddress = formatedAddress.split(",")[0];
                        } else
                            formatedAddress = "";

                        restaurant.setInfo(formatedAddress);
                        //restaurant.setHours(result.getPriceLevel().toString());
                        restaurant.setUrlPicture(result.getIcon());
                        restaurants.add(restaurant);
                    }
                    listRestaurant.postValue(restaurants);
                    // Memorize last restaurants
                    lastRestaurants.clear();
                    lastRestaurants.addAll(restaurants);
                } else {
                    Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() isSuccessful=false");
                }
            }

            @Override
            public void onFailure(Call<Textsearch> call, Throwable t) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
            }
        });

    }
}
