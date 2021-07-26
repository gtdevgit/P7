package com.example.go4lunch.ui.main.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.googleplace.model.Geometry;
import com.example.go4lunch.data.googleplace.model.OpeningHours;
import com.example.go4lunch.data.googleplace.model.autocomplete.Autocomplete;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.example.go4lunch.data.googleplace.model.placesearch.Result;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.testutils.LiveDataTestUtils;
import com.example.go4lunch.ui.main.model.Restaurant;
import com.example.go4lunch.ui.main.viewstate.MainViewState;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest extends TestCase {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    //private Location location = new Location("47.4055044477,0.698770997973");
    private Location location = Mockito.mock(android.location.Location.class);
    private final double fakeLatitude = 47.4055044477;
    private final double fakeLongitue = 0.698770997973;

    private PermissionChecker permissionChecker = Mockito.mock(PermissionChecker.class);
    private LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private FirestoreChosenRepository firestoreChosenRepository = Mockito.mock(FirestoreChosenRepository.class);
    private FirestoreLikedRepository firestoreLikedRepository = Mockito.mock(FirestoreLikedRepository.class);
    private FirestoreUsersRepository firestoreUsersRepository = Mockito.mock(FirestoreUsersRepository.class);
    private GooglePlacesApiRepository googlePlacesApiRepository = Mockito.mock(GooglePlacesApiRepository.class);

    private MutableLiveData<String> localFirestoreChosenRepositoryError;
    private MutableLiveData<String> localFirestoreLikedRepositoryError;
    private MutableLiveData<String> localFirestoreUsersRepositoryError;
    private MutableLiveData<String> localGooglePlacesApiRepositoryError;

    private MutableLiveData<Location> localLocationMutableLiveData;
    private MutableLiveData<List<UidPlaceIdAssociation>> localChosenRestaurantsLiveData;
    private MutableLiveData<List<UidPlaceIdAssociation>> localLikedRestaurantsLiveData;
    private MutableLiveData<List<User>> localUsersLiveData;
    private MutableLiveData<PlaceSearch> localPlaceSearchLiveData;
    private MutableLiveData<Autocomplete> localAutocompleteLiveData;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Log.d(Tag.TAG, "setUp() called");

        Mockito.doReturn(fakeLatitude).when(location).getLatitude();
        Mockito.doReturn(fakeLongitue).when(location).getLongitude();
        //Mockito.doReturn(1000).when(location).distanceTo(location2);

        // for configureErrorMediatorLiveData
        localFirestoreChosenRepositoryError = new MutableLiveData<>();
        localFirestoreLikedRepositoryError = new MutableLiveData<>();
        localFirestoreUsersRepositoryError = new MutableLiveData<>();
        localGooglePlacesApiRepositoryError = new MutableLiveData<>();
        Mockito.doReturn(localFirestoreChosenRepositoryError).when(firestoreChosenRepository).getErrorLiveData();
        Mockito.doReturn(localFirestoreLikedRepositoryError).when(firestoreLikedRepository).getErrorLiveData();
        Mockito.doReturn(localFirestoreUsersRepositoryError).when(firestoreUsersRepository).getErrorLiveData();
        Mockito.doReturn(localGooglePlacesApiRepositoryError).when(googlePlacesApiRepository).getErrorLiveData();

        // for configureMediatorLiveData
        localLocationMutableLiveData = new MutableLiveData<>();
        localChosenRestaurantsLiveData = new MutableLiveData<>();
        localLikedRestaurantsLiveData = new MutableLiveData<>();
        localUsersLiveData = new MutableLiveData<>();
        localPlaceSearchLiveData = new MutableLiveData<>();
        localAutocompleteLiveData = new MutableLiveData<>();

        Mockito.doReturn(localLocationMutableLiveData).when(locationRepository).getLocationLiveData();
        Mockito.doReturn(localChosenRestaurantsLiveData).when(firestoreChosenRepository).getChosenRestaurantsLiveData();
        Mockito.doReturn(localLikedRestaurantsLiveData).when(firestoreLikedRepository).getLikedRestaurantsLiveData();
        Mockito.doReturn(localUsersLiveData).when(firestoreUsersRepository).getUsersLiveData();
        Mockito.doReturn(localPlaceSearchLiveData).when(googlePlacesApiRepository).getNearbysearchLiveData();
        Mockito.doReturn(localAutocompleteLiveData).when(googlePlacesApiRepository).getAutocompleteLiveData();
    }


    private MainViewModel createViewModel(){
        return new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);
    }

    @Test
    public void testFirestoreChosenRepositoryError() throws InterruptedException {
        MainViewModel mainViewModel = new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        localFirestoreChosenRepositoryError.setValue("chosen restaurant error");
        String error = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getErrorMediatorLiveData(), 1);
        assertEquals(error, "chosen restaurant error");
    }

    @Test
    public void testFirestoreLikedRepositoryError() throws InterruptedException {
        MainViewModel mainViewModel = new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        localFirestoreLikedRepositoryError.setValue("liked restaurant error");
        String error = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getErrorMediatorLiveData(), 1);
        assertEquals(error, "liked restaurant error");
    }

    @Test
    public void testFirestoreUsersRepositoryError() throws InterruptedException {
        MainViewModel mainViewModel = new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        localFirestoreUsersRepositoryError.setValue("users error");
        String error = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getErrorMediatorLiveData(), 1);
        assertEquals(error, "users error");
    }

    @Test
    public void testGooglePlaceRepositoryError() throws InterruptedException {
        MainViewModel mainViewModel = new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        localGooglePlacesApiRepositoryError.setValue("Google place error");
        String error = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getErrorMediatorLiveData(), 1);
        assertEquals(error, "Google place error");
    }

    @Test
    public void testGetMainViewStateMediatorLiveData() throws InterruptedException {
        MainViewModel mainViewModel = new MainViewModel(
                permissionChecker,
                locationRepository,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        // dummy location
        localLocationMutableLiveData.setValue(location);
        // dummy users
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Dupond", "dupond@gamil.com", "https://www.picture/dupond.png"));
        users.add(new User("2", "Martin", "martin@gamil.com", "https://www.picture/martin.png"));
        localUsersLiveData.setValue(users);
        // dummy liked restaurant
        List<UidPlaceIdAssociation> likedRestaurants = new ArrayList<>();
        likedRestaurants.add(new UidPlaceIdAssociation("1", "1"));
        likedRestaurants.add(new UidPlaceIdAssociation("1", "2"));
        localLikedRestaurantsLiveData.setValue(likedRestaurants);
        // dummy chosen restaurants
        List<UidPlaceIdAssociation> chosenRestaurants = new ArrayList<>();
        //chosenRestaurants.add(new UidPlaceIdAssociation("1", "1"));
        localChosenRestaurantsLiveData.setValue(chosenRestaurants);
        // dummy place
        PlaceSearch placeSearch = new PlaceSearch();
        List<Result> results = new ArrayList<>();
        results.add(new Result("", "152 rue de l'Ermitage, Tours", "",
                new Geometry(
                        new com.example.go4lunch.data.googleplace.model.Location(47.4055044477, 0.698770997973), null ),
                "",
                "O152",
                new OpeningHours(true),
                null,
                "1",
                null,
                0,
                0,
                "",
                null,
                0,
                false));
        placeSearch.setResults(results);
        localPlaceSearchLiveData.setValue(placeSearch);

        // let's work the MainViewModel and get mainViewState
        MainViewState mainViewState = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getMainViewStateMediatorLiveData(), 1);

        // compare location
        double latitude = mainViewState.getLocation().getLatitude();
        assertEquals(latitude, location.getLatitude());
        double longitude = mainViewState.getLocation().getLongitude();
        assertEquals(longitude, location.getLongitude());
        // compare users
        assertEquals(users, mainViewState.getUsers());
        // compare restaurants
        Restaurant restaurant = new Restaurant(
                "1",
                "O152",
                fakeLatitude,
                fakeLongitue,
                0,
                "152 rue de l'Ermitage",
                2131820849,
                0,
                0,
                "",
                1);
        Restaurant mainViewStateRestaurant = mainViewState.getRestaurants().get(0);
        assertEquals(mainViewStateRestaurant.getPlaceId(), restaurant.getPlaceId());
        assertEquals(mainViewStateRestaurant.getName(), restaurant.getName());
        assertEquals(mainViewStateRestaurant.getLatitude(), restaurant.getLatitude());
        assertEquals(mainViewStateRestaurant.getLongitude(), restaurant.getLongitude());
        assertEquals(mainViewStateRestaurant.getDistance(), restaurant.getDistance());
        assertEquals(mainViewStateRestaurant.getInfo(), restaurant.getInfo());
        assertEquals(mainViewStateRestaurant.getOpenNowResourceString(), restaurant.getOpenNowResourceString());
        assertEquals(mainViewStateRestaurant.getWorkmatesCount(), restaurant.getWorkmatesCount());
        assertEquals(mainViewStateRestaurant.getRating(), restaurant.getRating());
        assertEquals(mainViewStateRestaurant.getUrlPicture(), restaurant.getUrlPicture());
        assertEquals(mainViewStateRestaurant.getCountLike(), restaurant.getCountLike());
    }
}