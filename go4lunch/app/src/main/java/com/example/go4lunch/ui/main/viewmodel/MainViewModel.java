package com.example.go4lunch.ui.main.viewmodel;

import android.annotation.SuppressLint;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.googleplace.model.OpeningHours;
import com.example.go4lunch.data.googleplace.model.autocomplete.Autocomplete;
import com.example.go4lunch.data.googleplace.model.autocomplete.Prediction;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.ui.main.model.CurrentUser;
import com.example.go4lunch.ui.main.model.Restaurant;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.googleplace.model.placesearch.Result;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.main.model.SearchViewResultItem;
import com.example.go4lunch.ui.main.model.SimpleRestaurant;
import com.example.go4lunch.ui.main.model.Workmate;
import com.example.go4lunch.ui.main.viewstate.MainViewState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    /**
     * repositories
     */
    @NonNull
    private GooglePlacesApiRepository googlePlacesApiRepository;
    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;
    private final FirestoreChosenRepository firestoreChosenRepository = new FirestoreChosenRepository();
    private final FirestoreUsersRepository firestoreUsersRepository = new FirestoreUsersRepository();
    private final FirestoreLikedRepository firestoreLikedRepository = new FirestoreLikedRepository();

    /**
     * MutableLiveData properties are exposed as livedata to prevent external modifications
     */

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    public LiveData<String> getErrorLiveData(){ return errorMutableLiveData; }

    private final MediatorLiveData<String> errorMediatorLiveData = new MediatorLiveData<>();
    public MediatorLiveData<String> getErrorMediatorLiveData() { return errorMediatorLiveData; }

    /**
     * Mediator expose MainViewState
     */
    private final MediatorLiveData<MainViewState> mainViewStateMediatorLiveData = new MediatorLiveData<>();
    public MediatorLiveData<MainViewState> getMainViewStateMediatorLiveData() { return mainViewStateMediatorLiveData; }

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

        configureErrorMediatorLiveData();
        configureMediatorLiveData();
    }

    private void configureErrorMediatorLiveData() {
        LiveData<String> googlePlacesErrorLiveData = googlePlacesApiRepository.getErrorLiveData();
        errorMediatorLiveData.addSource(googlePlacesErrorLiveData, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                errorMediatorLiveData.setValue(s);
            }
        });

        LiveData<String> firestoreUserRepositoryError = firestoreUsersRepository.getErrorLiveData();
        errorMediatorLiveData.addSource(firestoreUserRepositoryError, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                errorMediatorLiveData.setValue(s);
            }
        });

        LiveData<String> firestoreLikedRepositoryError = firestoreLikedRepository.getErrorLiveData();
        errorMediatorLiveData.addSource(firestoreLikedRepositoryError, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                errorMediatorLiveData.setValue(s);
            }
        });

        LiveData<String> firestoreChosenRepositoryError = firestoreChosenRepository.getErrorLiveData();
        errorMediatorLiveData.addSource(firestoreChosenRepositoryError, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                errorMediatorLiveData.setValue(s);
            }
        });
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
        LiveData<Autocomplete> autocompleteLiveData = googlePlacesApiRepository.getAutocompleteLiveData();

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
                            simpleRestaurantsLiveData.getValue(),
                            autocompleteLiveData.getValue());
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
                        simpleRestaurants,
                        autocompleteLiveData.getValue());
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
                        simpleRestaurantsLiveData.getValue(),
                        autocompleteLiveData.getValue());
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
                        simpleRestaurantsLiveData.getValue(),
                        autocompleteLiveData.getValue());
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
                        simpleRestaurantsLiveData.getValue(),
                        autocompleteLiveData.getValue());
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
                        simpleRestaurantsLiveData.getValue(),
                        autocompleteLiveData.getValue());
            }
        });

        // filter
        mainViewStateMediatorLiveData.addSource(searchTextMutableLiveData, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        chosenRestaurantsLiveData.getValue(),
                        usersLiveData.getValue(),
                        simpleRestaurantsLiveData.getValue(),
                        autocompleteLiveData.getValue());
            }
        });

        mainViewStateMediatorLiveData.addSource(autocompleteLiveData, new Observer<Autocomplete>() {
            @Override
            public void onChanged(Autocomplete autocomplete) {
                combine(locationLiveData.getValue(),
                        placeSearchLiveData.getValue(),
                        likedRestaurantsLiveData.getValue(),
                        chosenRestaurantsLiveData.getValue(),
                        usersLiveData.getValue(),
                        simpleRestaurantsLiveData.getValue(),
                        autocomplete);
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

    private int getOpenNowResourceString(OpeningHours openingHours){
        return (openingHours == null) ? R.string.empty_string : (openingHours.getOpenNow()) ? R.string.open_now : R.string.closing_now;
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

    private boolean isValideSearch(String searchText, String name){
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        return (name.toLowerCase().contains(searchText.toLowerCase().trim()));
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
                         @Nullable List<SimpleRestaurant> simpleRestaurants,
                         @Nullable Autocomplete autocomplete){
        // canot compute without data
        // autocomplete may be null
        if (location == null || placesearch == null || likedRestaurants == null ||
                chosenRestaurants == null || users == null || simpleRestaurants == null) {
            return;
        }

        List<Restaurant> restaurants = new ArrayList<>();
        for (Result result : placesearch.getResults()) {
            String name = result.getName();
            if (isValideSearch(searchText, name)) {
                String placeId = result.getPlaceId();
                double latitude = result.getGeometry().getLocation().getLat();
                double longitude = result.getGeometry().getLocation().getLng();
                float distance = calculateDistance(latitude, longitude, location);
                String info = findAddress(result.getFormattedAddress(), result.getVicinity());
                int openNowResourceString = getOpenNowResourceString(result.getOpeningHours());
                double rating = result.getRating();
                String urlPicture = findUrlPicture(result);
                int workmatesCount = countWorkmates(placeId, chosenRestaurants);
                int countLike = countLikeByRestaurant(placeId, likedRestaurants);
                Restaurant restaurant = new Restaurant(
                        placeId,
                        name,
                        latitude,
                        longitude,
                        distance,
                        info,
                        openNowResourceString,
                        workmatesCount,
                        rating,
                        urlPicture,
                        countLike);
                restaurants.add(restaurant);
            }
        }
        List<Workmate> workmates = createWorkmatesList(users, chosenRestaurants, simpleRestaurants);
        List<SearchViewResultItem> searchViewResultItems = new ArrayList<>();
        if (autocomplete != null && (autocomplete.getStatus().equals("OK")) && autocomplete.getPredictions() != null) {
            for (Prediction prediction : autocomplete.getPredictions()) {
                if (prediction.getTypes().contains("restaurant")) {
                    searchViewResultItems.add(new SearchViewResultItem(prediction.getDescription(), prediction.getPlaceId()));
                }
            }
        }

        mainViewStateMediatorLiveData.setValue(new MainViewState(location, restaurants, users, workmates, searchViewResultItems));
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

    private String searchText;

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        Log.d(Tag.TAG, "setSearchText() called with: searchText = [" + searchText + "]");
        searchTextMutableLiveData.setValue(searchText);
    }

    private MutableLiveData<String> searchTextMutableLiveData = new MutableLiveData<>();

    public void loadAutocomplet(String query){
        Location location = locationRepository.getLocationLiveData().getValue();
        if (location != null) {
            googlePlacesApiRepository.loadAutocomplete(location, query);
        }
    }

    /**
     * Current user
     */
    private final MutableLiveData<CurrentUser> currentUserMutableLiveData = new MutableLiveData<>();

    public LiveData<CurrentUser> getCurrentUserLiveData() {
        return currentUserMutableLiveData;
    }

    public void loadCurrentUser(){
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            currentUserMutableLiveData.setValue(new CurrentUser(
                    MainApplication.getApplication().getString(R.string.no_user_name_found),
                    MainApplication.getApplication().getString(R.string.no_user_email),
                    null));
        } else {
            String name = TextUtils.isEmpty(firebaseUser.getDisplayName()) ?
                    MainApplication.getApplication().getString(R.string.no_user_name_found) :
                    firebaseUser.getDisplayName();
            String email = TextUtils.isEmpty(firebaseUser.getEmail()) ?
                    MainApplication.getApplication().getString(R.string.no_user_email) :
                    firebaseUser.getEmail();
            Uri photoUrl = firebaseUser.getPhotoUrl();
            currentUserMutableLiveData.setValue(new CurrentUser(name, email, photoUrl));
        }
    }
}
