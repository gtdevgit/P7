package com.example.go4lunch.viewmodel;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.ui.model.Restaurant;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.googleplace.model.placesearch.Result;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    /**
     * GooglePlacesApiRepository
     */
    @NonNull
    private GooglePlacesApiRepository googlePlacesApiRepository;

    @NonNull
    private final PermissionChecker permissionChecker;

    @NonNull
    private final LocationRepository locationRepository;

    /**
     * MutableLiveData properties are exposed as livedata to prevent external modifications
     */
    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();

    public LiveData<List<Restaurant>> getRestaurantsLiveData() {return  this.restaurantsMutableLiveData; }
    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = new MutableLiveData<List<Restaurant>>();

    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<Location>();

    /**
     * Mediator expose MainViewState
     */
    private final MediatorLiveData<MainViewState> mainViewStateMediatorLiveData = new MediatorLiveData<>();
    public MediatorLiveData<MainViewState> getMainViewStateMediatorLiveData() { return mainViewStateMediatorLiveData; }

    /**
     * repositories
     */
    FirestoreChosenRepository firestoreChosenRepository = new FirestoreChosenRepository();
    FirestoreUsersRepository firestoreUsersRepository = new FirestoreUsersRepository();
    FirestoreLikedRepository firestoreLikedRepository = new FirestoreLikedRepository();

    public MainViewModel(
            GooglePlacesApiRepository googlePlacesApiRepository,
            PermissionChecker permissionChecker,
            LocationRepository locationRepository) {
        Log.d(Tag.TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData(){
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<UidPlaceIdAssociation>> chosenRestaurantsLiveData = firestoreChosenRepository.getChosenRestaurantsLiveData();
        LiveData<List<User>> usersLiveData = firestoreUsersRepository.getUsersLiveData();
        LiveData<List<UidPlaceIdAssociation>> likedRestaurantsLiveData = firestoreLikedRepository.getLikedRestaurantsLiveData();
        LiveData<PlaceSearch> placeSearchLiveData = googlePlacesApiRepository.getNearbysearchLiveData();

        mainViewStateMediatorLiveData.addSource(locationLiveData, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null) {
                    googlePlacesApiRepository.loadNearbysearch(location);
                    combine(location,
                            placeSearchLiveData.getValue(),
                            likedRestaurantsLiveData.getValue(),
                            chosenRestaurantsLiveData.getValue(),
                            usersLiveData.getValue());
                }
            }
        });

        mainViewStateMediatorLiveData.addSource(chosenRestaurantsLiveData, new Observer<List<UidPlaceIdAssociation>>() {
            @Override
            public void onChanged(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        uidPlaceIdAssociations,
                        usersLiveData.getValue());
            }
        });

        // all users collection
        mainViewStateMediatorLiveData.addSource(usersLiveData, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        chosenRestaurantsLiveData.getValue(),
                        users);
            }
        });

        // liked_restaurants collection for this restaurants
        mainViewStateMediatorLiveData.addSource(likedRestaurantsLiveData, new Observer<List<UidPlaceIdAssociation>>() {
            @Override
            public void onChanged(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        uidPlaceIdAssociations,
                        chosenRestaurantsLiveData.getValue(),
                        usersLiveData.getValue());
            }
        });

        //place search
        mainViewStateMediatorLiveData.addSource(placeSearchLiveData, new Observer<PlaceSearch>() {
            @Override
            public void onChanged(PlaceSearch placeSearch) {
                combine(locationLiveData.getValue(),
                        placeSearch,
                        likedRestaurantsLiveData.getValue(),
                        chosenRestaurantsLiveData.getValue(),
                        usersLiveData.getValue());
            }
        });
    }

    public void load(){
        refreshLocation();
        firestoreChosenRepository.loadAllChosenRestaurants();
        firestoreLikedRepository.loadLikedRestaurants();
        firestoreUsersRepository.loadAllUsers();

    }

    @SuppressLint("MissingPermission")
    private void refreshLocation() {
        // No GPS permission
        if (!permissionChecker.hasLocationPermission()) {
            locationRepository.stopLocationRequest();
        } else {
            locationRepository.startLocationRequest();
        }
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
        if (uidPlaceIdAssociations != null){
            for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations){
                if (uidPlaceIdAssociation.getPlaceId().equals(placeId)) {
                    result++;
                }
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
     *  combine : combine wait for all data before compute
     * @param location
     * @param placesearch
     * @param likedRestaurants
     * @param chosenRestaurants
     * @param workmates
     */
    private void combine(@Nullable Location location,
                         @Nullable PlaceSearch placesearch,
                         @Nullable List<UidPlaceIdAssociation> likedRestaurants,
                         @Nullable List<UidPlaceIdAssociation> chosenRestaurants,
                         @Nullable List<User> workmates){
        // canot compute without data
        if (location == null || placesearch == null || likedRestaurants == null || chosenRestaurants == null || workmates == null) {
            return;
        }

        List<Restaurant> restaurants = new ArrayList<>();
        for (Result result : placesearch.getResults()) {
            String palceId = result.getPlaceId();
            String name = result.getName();
            double latitude = result.getGeometry().getLocation().getLat();
            double longitude = result.getGeometry().getLocation().getLng();
            float distance = calculateDistance(latitude, longitude, location);
            String info = findAddress(result.getFormattedAddress(), result.getVicinity());
            String hours = "";
            double rating = result.getRating();
            String urlPicture = findUrlPicture(result);
            int workmatesCount = countWorkmates(palceId, chosenRestaurants);
            int countLike = countLikeByRestaurant(palceId, likedRestaurants);
            Restaurant restaurant = new Restaurant(
                    palceId,
                    name,
                    latitude,
                    longitude,
                    distance,
                    info,
                    hours,
                    workmatesCount,
                    rating,
                    urlPicture,
                    countLike);
            restaurants.add(restaurant);
        }
        //restaurantsMutableLiveData.postValue(restaurants);
        mainViewStateMediatorLiveData.setValue(new MainViewState(location, restaurants, workmates));
    }

    /**
     * to get real time change workmates count by restaurant
     */
    public void activateChosenRestaurantListener(){
        firestoreChosenRepository.activateRealTimeChosenListener();
    }

    public void removerChosenRestaurantListener(){
        firestoreChosenRepository.removeRealTimeChosenListener();
    }

    public void activateLikedRestaurantListener(){
        firestoreLikedRepository.activateRealTimeLikedListener();
    }

    public void removeLikedRestaurantListener(){
        firestoreLikedRepository.removeRealTimeLikedListener();
    }

    public void activateUsersRealTimeListener(){
        firestoreUsersRepository.activeRealTimeListener();
    }

    public void removeUsersRealTimeListener(){
        firestoreUsersRepository.removeRealTimeListener();
    }
}
