package com.example.go4lunch.ui.main.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.googleplace.model.autocomplete.Autocomplete;
import com.example.go4lunch.data.googleplace.model.placesearch.PlaceSearch;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.testutils.LiveDataTestUtils;
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

        Location location = new Location("47.4055044477,0.698770997973");
        localLocationMutableLiveData.setValue(location);

        List<User> users = new ArrayList<>();
        User user1 = new User("1", "Dupond", "dupond@gamil.com", "https://www.picture/dupond.png");
        users.add(user1);
        User user2 = new User("2", "Martin", "martin@gamil.com", "https://www.picture/martin.png");
        users.add(user2);
        localUsersLiveData.setValue(users);

        List<UidPlaceIdAssociation> likedRestaurants = new ArrayList<>();
        UidPlaceIdAssociation uidPlaceIdAssociation1 = new UidPlaceIdAssociation("1", "1");
        likedRestaurants.add(uidPlaceIdAssociation1);
        UidPlaceIdAssociation uidPlaceIdAssociation2 = new UidPlaceIdAssociation("1", "2");
        likedRestaurants.add(uidPlaceIdAssociation2);
        localLikedRestaurantsLiveData.setValue(likedRestaurants);

        List<UidPlaceIdAssociation> chosenRestaurants = new ArrayList<>();
        UidPlaceIdAssociation uidPlaceIdAssociation3 = new UidPlaceIdAssociation("1", "1");
        //chosenRestaurants.add(uidPlaceIdAssociation3);
        localChosenRestaurantsLiveData.setValue(chosenRestaurants);

        PlaceSearch placeSearchLiveData = new PlaceSearch();
        localPlaceSearchLiveData.setValue(placeSearchLiveData);


        MainViewState mainViewState = LiveDataTestUtils.getOrAwaitValue(mainViewModel.getMainViewStateMediatorLiveData(), 1);

        //mainViewModel.load();
        assertEquals(mainViewState.getLocation(), location);
    }


}