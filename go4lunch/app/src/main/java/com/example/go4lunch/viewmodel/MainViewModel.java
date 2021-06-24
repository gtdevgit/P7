package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.ChosenHelper;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.LikeHelper;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.ui.model.Restaurant;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.models.googleplaces.placesearch.Result;
import com.example.go4lunch.models.googleplaces.placesearch.PlaceSearch;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private ListenerRegistration registrationChosenRestaurant;
    private ListenerRegistration registrationLikedRestaurant;

    // for mediator
    private List<UidPlaceIdAssociation> likedCollection;
    private List<UidPlaceIdAssociation> chosenCollection;
    private List<Result> nearbysearch;

    /**
     * GooglePlacesApiRepository
     */
    private GooglePlacesApiRepository googlePlacesApiRepository;
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = new MutableLiveData<List<Restaurant>>();
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<Location>();

    public MainViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        Log.d(Tag.TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

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
    private int countUserRestaurantAssociationByPlaceId(String placeId, List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        int result = 0;
        for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations){
            if (uidPlaceIdAssociation.getPlaceId().equals(placeId)) {
                result++;
            }
        }
        return result;
    }

    /**
     * count likes by restaurants
     */
    private int countLikeByRestaurant(String placeId, List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        return countUserRestaurantAssociationByPlaceId(placeId, uidPlaceIdAssociations);
    }

    /**
     * count workmates who chose placeId
      * @param placeId
     * @param uidPlaceIdAssociations
     * @return
     */
    private int countWorkmates(String placeId, List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        return countUserRestaurantAssociationByPlaceId(placeId, uidPlaceIdAssociations);
    }

    /**
     * loadRestaurantAround : request data acquisition then launch combine to obtain the list of restaurants
     * @param location
     */
    private void loadRestaurantAround(Location location){
        Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround()");
        // - get liked collection to count like by placeId
        // - get chosen collection to count workmate by placeId
        // - get detail restaurants
        // - then combine(...)

        // initialization
        likedCollection = new ArrayList<>();
        chosenCollection = new ArrayList<>();
        nearbysearch = null;

        FailureListener failureListener = new FailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + e.getMessage());
                errorMutableLiveData.postValue(e.getMessage());
            }
        };

        // get likedCollection
        UserRestaurantAssociationListListener likedUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> userRestaurantAssociations) {
                likedCollection.addAll(userRestaurantAssociations);
                combine(location, likedCollection, chosenCollection, nearbysearch);
            }
        };
        LikeHelper.getLikedRestaurants(likedUserRestaurantAssociationListListener, failureListener);

        // get chosenCollection
        UserRestaurantAssociationListListener chosenUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> userRestaurantAssociations) {
                chosenCollection.addAll(userRestaurantAssociations);
                combine(location, likedCollection, chosenCollection, nearbysearch);
            }
        };
        ChosenHelper.getChosenRestaurants(chosenUserRestaurantAssociationListListener, failureListener);

        // get getNearbysearch
        Call<PlaceSearch> call = googlePlacesApiRepository.getNearbysearch(location);
        call.enqueue(new Callback<PlaceSearch>() {
            @Override
            public void onFailure(Call<PlaceSearch> call, Throwable t) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
                errorMutableLiveData.postValue(t.getMessage());
            }

            @Override
            public void onResponse(Call<PlaceSearch> call, Response<PlaceSearch> response) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onResponse() response.isSuccessful=" + response.isSuccessful());
                if (response.isSuccessful()) {
                    PlaceSearch placeSearch = response.body();
                    nearbysearch = placeSearch.getResults();
                    combine(location, likedCollection, chosenCollection, nearbysearch);
                }
            }
        });
    }

    /**
     * combine : combine wait for all data before compute
     * @param location
     * @param likedCollection
     * @param chosenCollection
     * @param nearbysearch
     */
    private void combine(@Nullable Location location,
                         @Nullable List<UidPlaceIdAssociation> likedCollection,
                         @Nullable List<UidPlaceIdAssociation> chosenCollection,
                         @Nullable List<Result> nearbysearch) {
        // canot compute without data
        if (location == null || likedCollection == null || chosenCollection == null || nearbysearch == null) {
            return;
        }

        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        for (Result result : nearbysearch) {
            String name = result.getName();
            double latitude = result.getGeometry().getLocation().getLat();
            double longitude = result.getGeometry().getLocation().getLng();
            float distance = calculateDistance(latitude, longitude, location);
            String info = findAddress(result.getFormattedAddress(), result.getVicinity());
            String hours = "";
            double rating = result.getRating();
            String urlPicture = findUrlPicture(result);
            int workmates = countWorkmates(result.getPlaceId(), chosenCollection);
            int countLike = countLikeByRestaurant(result.getPlaceId(), likedCollection);
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

    /**
     * to get real time change workmates count by restaurant
     */
    public void activateChosenRestaurantListener(){
        Log.d(Tag.TAG, "WorkmatesViewModel.activateUsersListener() called");
        registrationChosenRestaurant = ChosenHelper.getChosenCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void removerChosenRestaurantListener(){
        if (registrationChosenRestaurant != null) {
            Log.d(Tag.TAG, "WorkmatesViewModel.removeUsersListener() called");
            registrationChosenRestaurant.remove();
        }
    }

    public void activateLikedRestaurantListener(){
        Log.d(Tag.TAG, "activateLikedRestaurantListener() called");
        registrationLikedRestaurant = LikeHelper.getLikedCollection()
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void removeLikedRestaurantListener(){
        if (registrationLikedRestaurant != null) {
            Log.d(Tag.TAG, "WorkmatesViewModel.removeUsersListener() called");
            registrationLikedRestaurant.remove();
        }
    }

    /**
     * loadRestaurantAround
     * @param location
     */
    private void loadRestaurantAround_2(Location location){
        Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround()");
        // - get liked collection to count like by placeId
        // - get chosen collection to count workmate by placeId
        // - get detail restaurants
        // conbine(...)

        // raz
        likedCollection = new ArrayList<>();
        chosenCollection = new ArrayList<>();
        nearbysearch = null;

        FailureListener failureListener = new FailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + e.getMessage());
                errorMutableLiveData.postValue(e.getMessage());
            }
        };

        // get likedCollection
        UserRestaurantAssociationListListener likedUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> userRestaurantAssociations) {
                likedCollection.addAll(userRestaurantAssociations);
                combine(location, likedCollection, chosenCollection, nearbysearch);
            }
        };
        LikeHelper.getLikedRestaurants(likedUserRestaurantAssociationListListener, failureListener);

        // get chosenCollection
        UserRestaurantAssociationListListener chosenUserRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> userRestaurantAssociations) {
                chosenCollection.addAll(userRestaurantAssociations);
                combine(location, likedCollection, chosenCollection, nearbysearch);
            }
        };
        ChosenHelper.getChosenRestaurants(chosenUserRestaurantAssociationListListener, failureListener);

        // get getNearbysearch
        Call<PlaceSearch> call = googlePlacesApiRepository.getNearbysearch(location);
        call.enqueue(new Callback<PlaceSearch>() {
            @Override
            public void onFailure(Call<PlaceSearch> call, Throwable t) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onFailure() " + t.getMessage());
                errorMutableLiveData.postValue(t.getMessage());
            }

            @Override
            public void onResponse(Call<PlaceSearch> call, Response<PlaceSearch> response) {
                Log.d(Tag.TAG, "MainViewModel.loadRestaurantAround.onResponse() response.isSuccessful=" + response.isSuccessful());
                if (response.isSuccessful()) {
                    PlaceSearch placeSearch = response.body();
                    nearbysearch = placeSearch.getResults();
                    combine(location, likedCollection, chosenCollection, nearbysearch);
                }
            }
        });
    }

}
