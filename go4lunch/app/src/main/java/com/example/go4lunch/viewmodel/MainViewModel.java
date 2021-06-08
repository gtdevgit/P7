package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.ChoosenHelper;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.LikeHelper;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.models.googleplaces.placesearch.Result;
import com.example.go4lunch.models.googleplaces.placesearch.PlaceSearch;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    /**
     * /////////////////////// nested interface ///////////////////////
     * callback loadRestaurants
     */
    private interface LoadRestaurantListener{
        void onLoadCompleted(List<Restaurant> restaurants);
    }

    private LoadRestaurantListener loadRestaurantListener;

    private ListenerRegistration registrationChoosenRestaurant;

    /**
     * GooglePlacesApiRepository
     */
    private GooglePlacesApiRepository googlePlacesApiRepository;
    private final MutableLiveData<Autocomplete> autocompleteMutableLiveData = new MutableLiveData<Autocomplete>();
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = new MutableLiveData<List<Restaurant>>();
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<Location>();

    public MainViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        Log.d(Tag.TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;

        loadRestaurantListener = new LoadRestaurantListener() {
            @Override
            public void onLoadCompleted(List<Restaurant> restaurants) {
                restaurantsMutableLiveData.postValue(restaurants);
            }
        };
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
     * setLocation notify the view model that the GPS location has changed
     * @param location
     */
    public void setLocation(Location location){
        Log.d(Tag.TAG, "MainViewModel.setLocation(location) " + location.getLatitude() + ", " + location.getLongitude());
        this.locationMutableLiveData.postValue(location);
        // reload restaurants
        this.loadRestaurantAround(location);
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
     * count placeId record in List<UserRestaurantAssociation>
     */
    private int countUserRestaurantAssociationByPlaceId(String placeId, List<UserRestaurantAssociation> userRestaurantAssociations){
        int result = 0;
        for (UserRestaurantAssociation userRestaurantAssociation : userRestaurantAssociations){
            if (userRestaurantAssociation.getPlaceId().equals(placeId)) {
                result++;
            }
        }
        return result;
    }

    /**
     * count likes by restaurants
     */
    private int countLikeByRestaurant(String placeId, List<UserRestaurantAssociation> userRestaurantAssociations){
        return countUserRestaurantAssociationByPlaceId(placeId, userRestaurantAssociations);
    }

    /**
     * count workmates who chose placeId
      * @param placeId
     * @param userRestaurantAssociations
     * @return
     */
    private int countWorkmates(String placeId, List<UserRestaurantAssociation> userRestaurantAssociations){
        return countUserRestaurantAssociationByPlaceId(placeId, userRestaurantAssociations);
    }

    /**
     * loadRestaurantAround
     * @param location
     */
    private void loadRestaurantAround(Location location){
        Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround()");

        List<UserRestaurantAssociation> likedUserRestaurants = new ArrayList<>();
        List<UserRestaurantAssociation> chossenUserRestaurants = new ArrayList<>();

        FailureListener failureListener = new FailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + e.getMessage());
                errorMutableLiveData.postValue(e.getMessage());
            }
        };

        UserRestaurantAssociationListListener choosenUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations) {
                Call<PlaceSearch> call = googlePlacesApiRepository.getNearbysearch(location);
                call.enqueue(new Callback<PlaceSearch>() {
                    @Override
                    public void onResponse(Call<PlaceSearch> call, Response<PlaceSearch> response) {
                        Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onResponse() response.isSuccessful=" + response.isSuccessful());
                        if (response.isSuccessful()) {
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
                                double rating = result.getRating();
                                String urlPicture = findUrlPicture(result);
                                int workmates = countWorkmates(result.getPlaceId(), userRestaurantAssociations);
                                int countLike = countLikeByRestaurant(result.getPlaceId(), likedUserRestaurants);
                                Restaurant restaurant = new Restaurant(result.getPlaceId(),
                                        name,
                                        latitude,
                                        longitude,
                                        distance,
                                        info,
                                        hours,
                                        workmates,
                                        rating,
                                        urlPicture,
                                        countLike);
                                restaurants.add(restaurant);
                            }
                            restaurantsMutableLiveData.postValue(restaurants);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceSearch> call, Throwable t) {
                        Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
                        errorMutableLiveData.postValue(t.getMessage());
                    }
                });
            }
        };

        UserRestaurantAssociationListListener likedUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations) {
                likedUserRestaurants.addAll(userRestaurantAssociations);
                ChoosenHelper.getChoosenRestaurants(choosenUserRestaurantAssociationListListener, failureListener);
            }
        };

        // first : get liked collection to count like by placeId
        // second : get chosen collection to count workmate by placeId
        // third : get detail restaurants
        LikeHelper.getLikedRestaurants(likedUserRestaurantAssociationListListener, failureListener);
    };

    /**
     * to get real time change workmates count by restaurant
     */
    public void activateChoosenRestaurantListener(){
        Log.d(Tag.TAG, "WorkmatesViewModel.activateUsersListener() called");
        registrationChoosenRestaurant = ChoosenHelper.getChoosenCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null){
                    errorMutableLiveData.postValue(error.getMessage());
                    return;
                }
                loadRestaurantAround(getLocationLiveData().getValue());
            }
        });
    }

    public void removerChoosenRestaurantListener(){
        if (registrationChoosenRestaurant != null) {
            Log.d(Tag.TAG, "WorkmatesViewModel.removeUsersListener() called");
            registrationChoosenRestaurant.remove();
        }
    }

}
