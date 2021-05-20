package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.googleplaces.placesearch.Result;
import com.example.go4lunch.models.googleplaces.placesearch.PlaceSearch;
import com.example.go4lunch.repository.GooglePlacesApiRepository;

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
        this.loadRestaurantAround(location);
    }

    /**
     * this data is kept in cache
     */
    public List<Restaurant> getRestaurantsCache() {
        return restaurantsCache;
    }

    /**
     * calculates the distance between a location and a point which is defined by latitute and longitude
     * @param latitude
     * @param longitude
     * @param location
     * @return
     */
    private float calculateDistance(double latitude, double longitude, Location location) {
        Location destinationLocation = new Location("");
        destinationLocation.setLatitude(latitude);
        destinationLocation.setLongitude(longitude);
        return location.distanceTo(destinationLocation);
    }

    /**
     * Compatibility with getTextsearch and getNearbysearch:
     * returns the first part of the address found in formatedAddress or vicinity
     * @param formattedAddress
     * @param vicinity
     * @return
     */
    private String findAddress(String formattedAddress, String vicinity){
        String address = "";
        if (formattedAddress != null) {
            address = formattedAddress;
        } else {
            if (vicinity != null)
                address = vicinity;
        }

        if ((address != null) && (address.trim().length() > 0) && (address.indexOf(',') >= 0)) {
            address = address.split(",")[0];
        }
        return address;
    }

    /**
     * the photo can be in photorefrence or in the icon
     * @param result
     * @return
     */
    private String findUrlPicture(Result result){
        String url;
        if ((result.getPhotos() != null) && (result.getPhotos().size() >= 1)) {
            url = googlePlacesApiRepository.getUrlPlacePhoto(result.getPhotos().get(0).getPhotoReference());
        } else {
            url = result.getIcon();
        }
        return url;
    }

    /**
     * loadRestaurantAround
     * @param location
     */
    private void loadRestaurantAround(Location location){
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: location = [" + location + "]");
        Log.d(TAG, "MainViewModel.loadRestaurantAround() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        //Call<Textsearch> call = googlePlacesApiRepository.getTextsearch("restaurant", location);
        Call<PlaceSearch> call = googlePlacesApiRepository.getNearbysearch(location);
        call.enqueue(new Callback<PlaceSearch>() {
            @Override
            public void onResponse(Call<PlaceSearch> call, Response<PlaceSearch> response) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()){
                    Log.d(TAG, "MainViewModel.loadRestaurantAround.onResponse() isSuccessful=true");
                    PlaceSearch placeSearch = response.body();
                    List<Result> lstResult = placeSearch.getResults();
                    List<Restaurant> restaurants = new ArrayList<Restaurant>();
                    for (Result result : lstResult) {
                        String name = result.getName();
                        double latitude = result.getGeometry().getLocation().getLat();
                        double longitude = result.getGeometry().getLocation().getLng();
                        float distance = calculateDistance(latitude, longitude, location);
                        String info = findAddress(result.getFormattedAddress(), result.getVicinity());
                        String hours = "";
                        String workmates = "";
                        String rating = result.getRating().toString();
                        String urlPicture = findUrlPicture(result);

                        Restaurant restaurant = new Restaurant(result.getPlaceId(),
                                name,
                                latitude,
                                longitude,
                                distance,
                                info,
                                hours,
                                workmates,
                                rating,
                                urlPicture);
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
            public void onFailure(Call<PlaceSearch> call, Throwable t) {
                Log.d(TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
                errorMutableLiveData.postValue(t.getMessage());
            }
        });

    }
}
