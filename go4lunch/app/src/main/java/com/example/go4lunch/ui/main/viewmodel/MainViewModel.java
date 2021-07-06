package com.example.go4lunch.ui.main.viewmodel;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.ui.main.model.Restaurant;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.googleplace.model.placesearch.Result;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.main.model.SimpleRestaurant;
import com.example.go4lunch.ui.main.model.Workmate;
import com.example.go4lunch.ui.main.viewstate.MainViewState;

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

    GetPlaceDetailsByPlaceIds getPlaceDetailsByPlaceIds;

    public MainViewModel(
            GooglePlacesApiRepository googlePlacesApiRepository,
            PermissionChecker permissionChecker,
            LocationRepository locationRepository) {
        Log.d(Tag.TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
        getPlaceDetailsByPlaceIds = new GetPlaceDetailsByPlaceIds(googlePlacesApiRepository);

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData(){
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<UidPlaceIdAssociation>> chosenRestaurantsLiveData = firestoreChosenRepository.getChosenRestaurantsLiveData();
        // placeIdsLiveData: To get a list of place id
        // chosenRestaurantsLiveData->placeIdsLiveData
        LiveData<List<String>> placeIdsLiveData = Transformations.map(chosenRestaurantsLiveData, this::UidPlaceIdAssociationListToPlaceIdList);
        // simpleRestaurantsLiveData: to get détails of placeids
        // placeIdsLiveData->simpleRestaurantsLiveData
        LiveData<List<SimpleRestaurant>> simpleRestaurantsLiveData = Transformations.switchMap(placeIdsLiveData, getPlaceDetailsByPlaceIds::get);
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
                            usersLiveData.getValue(),
                            simpleRestaurantsLiveData.getValue());
                }
            }
        });

        // prepare Observer
        Observer<List<SimpleRestaurant>> simpleRestaurantsObserver = new Observer<List<SimpleRestaurant>>() {
            @Override
            public void onChanged(List<SimpleRestaurant> simpleRestaurants) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        chosenRestaurantsLiveData.getValue(),
                        usersLiveData.getValue(),
                        simpleRestaurants);
            }
        };
        mainViewStateMediatorLiveData.addSource(simpleRestaurantsLiveData, simpleRestaurantsObserver);

        mainViewStateMediatorLiveData.addSource(chosenRestaurantsLiveData, new Observer<List<UidPlaceIdAssociation>>() {
            @Override
            public void onChanged(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        uidPlaceIdAssociations,
                        usersLiveData.getValue(),
                        simpleRestaurantsLiveData.getValue());
            }
        });

        mainViewStateMediatorLiveData.addSource(placeIdsLiveData, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
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
                        users,
                        simpleRestaurantsLiveData.getValue());
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
                        usersLiveData.getValue(),
                        simpleRestaurantsLiveData.getValue());
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
                        usersLiveData.getValue(),
                        simpleRestaurantsLiveData.getValue());
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

    private int indexOfUid(String uid, List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        for (int i=0; i<uidPlaceIdAssociations.size(); i++){
            UidPlaceIdAssociation uidPlaceIdAssociation = uidPlaceIdAssociations.get(i);
            if (uid.equals(uidPlaceIdAssociation.getUserUid())) {
                return i;
            }
        }
        return -1;
    }

    private List<String> UidPlaceIdAssociationListToPlaceIdList(List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        List<String> placeIds = new ArrayList<>();
        for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations) {
            placeIds.add(uidPlaceIdAssociation.getPlaceId());
        }
        return placeIds;
    }

    private String findPlaceId(String uid, List<UidPlaceIdAssociation> chosenRestaurants){
        int idx = indexOfUid(uid, chosenRestaurants);
        if (idx == -1) return "";
        return chosenRestaurants.get(idx).getPlaceId();
    }

    private int indexOfplaceId(String placeId, List<SimpleRestaurant> simpleRestaurants){
        for (int i=0; i<simpleRestaurants.size(); i++){
            SimpleRestaurant simpleRestaurant = simpleRestaurants.get(i);
            if (placeId.equals(simpleRestaurant.getPlaceId())) {
                return i;
            }
        }
        return -1;
    }

    private String findPlaceIdName(String placeId, List<SimpleRestaurant> simpleRestaurants){
        int idx = indexOfplaceId(placeId, simpleRestaurants);
        if (idx == -1) return "";
        return simpleRestaurants.get(idx).getName();
    }


    private List<Workmate> createWorkmatesList(List<User> users,
                                               List<UidPlaceIdAssociation> chosenRestaurants,
                                               List<SimpleRestaurant> simpleRestaurants){
        List<Workmate> workmates = new ArrayList<>();
        for (User user : users){
            String userName = user.getUserName();
            String userUrlPicture = user.getUrlPicture();
            String placeId = findPlaceId(user.getUid(), chosenRestaurants);
            String restaurantName = findPlaceIdName(placeId, simpleRestaurants);
            workmates.add(new Workmate(userName, userUrlPicture, placeId, restaurantName));
        }
        return workmates;
    }

    /**
     *  combine : combine wait for all data before compute
     * @param location
     * @param placesearch
     * @param likedRestaurants
     * @param chosenRestaurants
     * @param simpleRestaurants
     */
    private void combine(@Nullable Location location,
                         @Nullable PlaceSearch placesearch,
                         @Nullable List<UidPlaceIdAssociation> likedRestaurants,
                         @Nullable List<UidPlaceIdAssociation> chosenRestaurants,
                         @Nullable List<User> users,
                         @Nullable List<SimpleRestaurant> simpleRestaurants){
        // canot compute without data
        if (location == null || placesearch == null || likedRestaurants == null ||
                chosenRestaurants == null || users == null || simpleRestaurants == null) {
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
        List<Workmate> workmates = createWorkmatesList(users, chosenRestaurants, simpleRestaurants);
        //restaurantsMutableLiveData.postValue(restaurants);
        mainViewStateMediatorLiveData.setValue(new MainViewState(location, restaurants, users, workmates));
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
