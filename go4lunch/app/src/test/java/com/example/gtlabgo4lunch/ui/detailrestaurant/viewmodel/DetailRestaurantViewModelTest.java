package com.example.gtlabgo4lunch.ui.detailrestaurant.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.gtlabgo4lunch.data.firestore.model.User;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.gtlabgo4lunch.data.googleplace.model.Geometry;
import com.example.gtlabgo4lunch.data.googleplace.model.Location;
import com.example.gtlabgo4lunch.data.googleplace.model.OpeningHours;
import com.example.gtlabgo4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.gtlabgo4lunch.data.googleplace.model.placedetails.Result;
import com.example.gtlabgo4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.gtlabgo4lunch.testutils.LiveDataTestUtils;
import com.example.gtlabgo4lunch.ui.detailrestaurant.viewstate.DetailRestaurantViewState;

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
public class DetailRestaurantViewModelTest extends TestCase {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FirestoreChosenRepository firestoreChosenRepository = Mockito.mock(FirestoreChosenRepository.class);
    private FirestoreLikedRepository firestoreLikedRepository = Mockito.mock(FirestoreLikedRepository.class);
    private FirestoreUsersRepository firestoreUsersRepository = Mockito.mock(FirestoreUsersRepository.class);
    private GooglePlacesApiRepository googlePlacesApiRepository = Mockito.mock(GooglePlacesApiRepository.class);

    private String currentUid;
    MutableLiveData<List<UidPlaceIdAssociation>> localChosenRestaurantsByPlaceIdLiveData;
    MutableLiveData<List<User>> localUsersLiveData;
    MutableLiveData<List<UidPlaceIdAssociation>> localLikedRestaurantsByPlaceIdLiveData;
    MutableLiveData<PlaceDetails> localPlaceDetailsLiveData;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        localChosenRestaurantsByPlaceIdLiveData = new MutableLiveData<>();
        localUsersLiveData = new MutableLiveData<>();
        localLikedRestaurantsByPlaceIdLiveData = new MutableLiveData<>();
        localPlaceDetailsLiveData = new MutableLiveData<>();

        currentUid = "1";
        Mockito.doReturn(localChosenRestaurantsByPlaceIdLiveData).when(firestoreChosenRepository).getChosenRestaurantsByPlaceIdLiveData();
        Mockito.doReturn(localUsersLiveData).when(firestoreUsersRepository).getUsersByUidsMutableLiveData();
        Mockito.doReturn(localLikedRestaurantsByPlaceIdLiveData).when(firestoreLikedRepository).getLikedRestaurantsByPlaceIdLiveData();
        Mockito.doReturn(localPlaceDetailsLiveData).when(googlePlacesApiRepository).getPlaceDetailsLiveData();
    }

    @Test
    public void testGetDetailRestaurantViewStateLiveData() throws InterruptedException {
        DetailRestaurantViewModel detailRestaurantViewModel = new DetailRestaurantViewModel(
                currentUid,
                firestoreChosenRepository,
                firestoreLikedRepository,
                firestoreUsersRepository,
                googlePlacesApiRepository);

        List<UidPlaceIdAssociation> chosenRestaurants = new ArrayList<>();
        // user 1 chose placeId 1
        chosenRestaurants.add(new UidPlaceIdAssociation("1", "1"));
        chosenRestaurants.add(new UidPlaceIdAssociation("2", "1"));
        localChosenRestaurantsByPlaceIdLiveData.setValue(chosenRestaurants);

        List<UidPlaceIdAssociation> likedRestaurants = new ArrayList<>();
        // user 1 like placeId 1
        likedRestaurants.add(new UidPlaceIdAssociation("1", "1"));
        likedRestaurants.add(new UidPlaceIdAssociation("2", "1"));
        localLikedRestaurantsByPlaceIdLiveData.setValue(likedRestaurants);

        // dummy users
        List<User> users = new ArrayList<>();
        users.add(new User("1", "Dupond", "dupond@gamil.com", "https://www.picture/dupond.png"));
        users.add(new User("2", "Martin", "martin@gamil.com", "https://www.picture/martin.png"));
        localUsersLiveData.setValue(users);

        String placeId = "1";
        String name = "Ô 152";
        String address = "152 rue de l'Ermitage, 37100 Tours";
        String expectedAddress = "152 rue de l'Ermitage";
        String phone = "33 6 64 03 91 68";
        String website = "https://www.o152.fr/";
        double latitude = 47.4055044477;
        double longitue = 0.698770997973;

        PlaceDetails placeDetails = new PlaceDetails();
        placeDetails.setResult(new Result(null,
                null,
                null,
                address,
                phone,
                new Geometry(
                        new Location(latitude, longitue), null ),
                "",
                phone,
                name,
                new OpeningHours(false),
                null,
                placeId,
                null,
                0,
                0,
                "",
                null,
                null,
                website,
                0,
                0,
                "",
                website));
        localPlaceDetailsLiveData.setValue(placeDetails);

        DetailRestaurantViewState detailRestaurantViewState = LiveDataTestUtils.getOrAwaitValue(
                detailRestaurantViewModel.getDetailRestaurantViewStateLiveData(), 1);

        assertEquals(detailRestaurantViewState.getPlaceId(), placeId);
        assertEquals(detailRestaurantViewState.getName(), name);
        assertEquals(detailRestaurantViewState.getInfo(), expectedAddress);
        assertEquals(detailRestaurantViewState.getPhoneNumber(),phone);
        assertEquals(detailRestaurantViewState.getWebsite(), website);
        assertEquals(detailRestaurantViewState.isChosenByCurrentUser(), true);
        assertEquals(detailRestaurantViewState.isLikedByCurrentUser(), true);
        assertEquals(detailRestaurantViewState.getStar1Color(), R.color.yellow);
        assertEquals(detailRestaurantViewState.getStar2Color(), R.color.yellow);
        assertEquals(detailRestaurantViewState.getStar3Color(), R.color.white);
    }
}