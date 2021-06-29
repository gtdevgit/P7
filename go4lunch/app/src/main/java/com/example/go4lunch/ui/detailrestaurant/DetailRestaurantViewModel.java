package com.example.go4lunch.ui.detailrestaurant;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.ui.model.DetailRestaurantViewState;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.googleplace.model.Photo;
import com.example.go4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.go4lunch.ui.model.SimpleUserViewState;
import com.example.go4lunch.repository.GooglePlacesApiRepository;

import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantViewModel extends ViewModel {

    private final int STAR_LEVEL_1 = 1;
    private final int STAR_LEVEL_2 = 2;
    private final int STAR_LEVEL_3 = 3;

    private GooglePlacesApiRepository googlePlacesApiRepository;
    // cache
    private CacheDetailRestaurantViewModel cache;

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    private FirestoreUsersRepository firestoreUsersRepository;
    private FirestoreChosenRepository firestoreChosenRepository;
    private FirestoreLikedRepository firestoreLikedRepository;

    private final MediatorLiveData<DetailRestaurantViewState> detailRestaurantViewStateMediatorLiveData = new MediatorLiveData<>();
    public LiveData<DetailRestaurantViewState> getDetailRestaurantViewStateLiveData() {
        return detailRestaurantViewStateMediatorLiveData;
    }

    public DetailRestaurantViewModel(GooglePlacesApiRepository googlePlacesApiRepository,
                                     String currentUid) {

        this.googlePlacesApiRepository = googlePlacesApiRepository;
        cache = new CacheDetailRestaurantViewModel(currentUid);

        firestoreUsersRepository = new FirestoreUsersRepository();
        firestoreChosenRepository = new FirestoreChosenRepository();
        firestoreLikedRepository = new FirestoreLikedRepository();

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData(){
        LiveData<List<UidPlaceIdAssociation>> chosenRestaurantsByPlaceIdLiveData = firestoreChosenRepository.getChosenRestaurantsByPlaceIdLiveData();
        LiveData<List<User>> usersLiveData = firestoreUsersRepository.getUsersByUidsMutableLiveData();
        LiveData<List<SimpleUserViewState>> simpleUserViewStatesLiveData = Transformations.map(usersLiveData, this::usersListToSimpleUserViewStateList);
        LiveData<List<UidPlaceIdAssociation>> likedRestaurantsByPlaceIdLiveData = firestoreLikedRepository.getLikedRestaurantsByPlaceIdLiveData();
        LiveData<PlaceDetails> placeDetailsLiveData = googlePlacesApiRepository.getPlaceDetailsLiveData();

        // chosen_collection for this restaurant
        // will claim the corresponding users data
        detailRestaurantViewStateMediatorLiveData.addSource(chosenRestaurantsByPlaceIdLiveData, new Observer<List<UidPlaceIdAssociation>>() {
            @Override
            public void onChanged(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                // transform to List Uid
                List<String> uids = UidPlaceIdAssociationListToUidList(uidPlaceIdAssociations);
                // we have uid, now we need users objects
                firestoreUsersRepository.loadUsersByUids(uids);
                combine(placeDetailsLiveData.getValue(),
                        likedRestaurantsByPlaceIdLiveData.getValue(),
                        uidPlaceIdAssociations,
                        simpleUserViewStatesLiveData.getValue());
            }
        });

        // users collection for this restaurant.
        detailRestaurantViewStateMediatorLiveData.addSource(usersLiveData, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
            }
        });

        // simple users who chose this restaurant.
        // comme from transformation of usersLiveData
        detailRestaurantViewStateMediatorLiveData.addSource(simpleUserViewStatesLiveData, new Observer<List<SimpleUserViewState>>() {
            @Override
            public void onChanged(List<SimpleUserViewState> simpleUserViewStates) {
                combine(placeDetailsLiveData.getValue(),
                        likedRestaurantsByPlaceIdLiveData.getValue(),
                        chosenRestaurantsByPlaceIdLiveData.getValue(),
                        simpleUserViewStatesLiveData.getValue());
            }
        });

        // liked_restaurants collection for this restaurants
        detailRestaurantViewStateMediatorLiveData.addSource(likedRestaurantsByPlaceIdLiveData, new Observer<List<UidPlaceIdAssociation>>() {
            @Override
            public void onChanged(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                // know id the current user have liked restaurant
                // know the count to assign stars colors
                combine(placeDetailsLiveData.getValue(),
                        uidPlaceIdAssociations,
                        chosenRestaurantsByPlaceIdLiveData.getValue(),
                        simpleUserViewStatesLiveData.getValue());
            }
        });

        //place detail
        detailRestaurantViewStateMediatorLiveData.addSource(placeDetailsLiveData, new Observer<PlaceDetails>() {
            @Override
            public void onChanged(PlaceDetails placeDetails) {
                combine(placeDetails,
                        likedRestaurantsByPlaceIdLiveData.getValue(),
                        chosenRestaurantsByPlaceIdLiveData.getValue(),
                        simpleUserViewStatesLiveData.getValue());
            }
        });
    }

    public void load(String placeId){
        cache.setPlaceId(placeId);
        firestoreChosenRepository.loadChosenRestaurantsByPlaceId(cache.getPlaceId());
        firestoreLikedRepository.loadLikedRestaurantsByPlaceId(cache.getPlaceId());
        googlePlacesApiRepository.loadDetails(cache.getPlaceId());
    }

    private SimpleUserViewState userToSimpleUserViewState(User user){
        return new SimpleUserViewState(user.getUserName(), user.getUrlPicture());
    }

    private List<SimpleUserViewState> usersListToSimpleUserViewStateList(List<User> users){
        List<SimpleUserViewState> simpleUserViewStateList = new ArrayList<>();
        for (User user : users){
            simpleUserViewStateList.add(userToSimpleUserViewState(user));
        }
        return simpleUserViewStateList;
    }

    /**
     * transforms one List<UserRestaurantAssociation> to one List<String> containing each uid
     * @param uidPlaceIdAssociations
     * @return
     */
    private List<String> UidPlaceIdAssociationListToUidList(List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        List<String> uidList = new ArrayList<>();
        for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations) {
            uidList.add(uidPlaceIdAssociation.getUserUid());
        }
        return uidList;
    }

    public void changeLike(){
        if (cache.isLikedByCurrentUser()) {
            unlike(cache.getUid(), cache.getPlaceId());
        } else {
            like(cache.getUid(), cache.getPlaceId());
        }
        firestoreLikedRepository.loadLikedRestaurantsByPlaceId(cache.getPlaceId());
    }

    private void like(String uid, String placeId){
        firestoreLikedRepository.createLike(uid, placeId);
    }

    private void unlike(String uid, String placeId) {
        firestoreLikedRepository.deleteLike(uid, placeId);
    }

    public void changeChose(){
        if (cache.isChosenByCurrentUser()) {
            unchoose(cache.getUid());
        } else {
            choose(cache.getUid(), cache.getPlaceId());
        }
        // update new data
        firestoreChosenRepository.loadChosenRestaurantsByPlaceId(cache.getPlaceId());
    }

    /**
     * user choose restaurant
     * @param uid
     * @param placeId
     */
    private void choose(String uid, String placeId){
        firestoreChosenRepository.createChosenRestaurant(uid, placeId);
    }

    /**
     * user unchoose restaurant
     * @param uid
     */
    private void unchoose(String uid) {
        firestoreChosenRepository.deleteChosenRestaurant(uid);
    }

    /**
     * for real time workmates list
     */
    public void activateWormatesByPlaceListener(){
        firestoreChosenRepository.activateRealTimeChosenByPlaceListener(cache.getPlaceId());
    };

    public void removeWormatesByPlaceListener(){
        firestoreChosenRepository.removeRealTimeChosenByPlaceListener();
    };

    /**
     * for real time liked
     */
    public void activateLikedByPlaceListener(){
        firestoreLikedRepository.activateRealTimeLikedByPlaceListener(cache.getPlaceId());
    };

    public void removeLikedByPlaceListener(){
        firestoreLikedRepository.removeRealTimeLikedByPlaceListener();
    };

    private boolean isContainUid(String uid, List<UidPlaceIdAssociation> list){
        for (UidPlaceIdAssociation uidPlaceIdAssociation : list){
            if (uid.equals(uidPlaceIdAssociation.getUserUid())) {
                return true;
            }
        }
        return false;
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

    private boolean getStarByLevel(int level, int likeCounter){
        return (likeCounter >= level);
    }

    private int getStarColorByLevel(int level, int likeCounter){
        if (getStarByLevel(level, likeCounter)) {
            return R.color.yellow;
        } else {
            return R.color.white;
        }
    }

    /**
     * Where we get all data we can emit one detailRestaurantViewState
     * @param placeDetails
     * @param likedRestaurantsByPlaceId
     * @param chosenRestaurantsByPlaceId
     * @param workmatesByPlaceId
     */
    private void combine(@Nullable PlaceDetails placeDetails,
                         @Nullable List<UidPlaceIdAssociation> likedRestaurantsByPlaceId,
                         @Nullable List<UidPlaceIdAssociation> chosenRestaurantsByPlaceId,
                         @Nullable List<SimpleUserViewState> workmatesByPlaceId) {
        if ((placeDetails == null) || (likedRestaurantsByPlaceId == null) ||
                (chosenRestaurantsByPlaceId == null) || (workmatesByPlaceId == null)){
            return;
        }

        String placeId = placeDetails.getResult().getPlaceId();
        List<String> urlPhotos = new ArrayList<>();
        if (placeDetails.getResult().getPhotos() != null) {
            for (Photo photo : placeDetails.getResult().getPhotos()){
                if ((photo.getPhotoReference() != null) && (photo.getPhotoReference().trim().length() > 0)) {
                    urlPhotos.add(googlePlacesApiRepository.getUrlPlacePhoto(photo.getPhotoReference()));
                }
            }
        }
        String urlPicture = (urlPhotos.size() > 0) ? urlPhotos.get(0) : null;
        String name = placeDetails.getResult().getName();
        String info = findAddress(placeDetails.getResult().getFormattedAddress(), placeDetails.getResult().getVicinity());
        String phoneNumber = placeDetails.getResult().getInternationalPhoneNumber();
        String website = placeDetails.getResult().getWebsite();

        int likeCounter = likedRestaurantsByPlaceId.size();
        int star1Color = getStarColorByLevel(STAR_LEVEL_1, likeCounter);
        int star2Color = getStarColorByLevel(STAR_LEVEL_2, likeCounter);
        int star3Color = getStarColorByLevel(STAR_LEVEL_3, likeCounter);

        boolean likedByCurrentUser = isContainUid(cache.getUid(), likedRestaurantsByPlaceId);
        boolean chosenByCurrentUser = isContainUid(cache.getUid(), chosenRestaurantsByPlaceId);

        cache.setLikedByCurrentUser(likedByCurrentUser);
        cache.setChosenByCurrentUser(chosenByCurrentUser);

        DetailRestaurantViewState detailRestaurantViewState = new DetailRestaurantViewState(
                placeId,
                urlPicture,
                name,
                info,
                chosenByCurrentUser,
                star1Color,
                star2Color,
                star3Color,
                phoneNumber,
                likedByCurrentUser,
                website,
                workmatesByPlaceId
        );
        detailRestaurantViewStateMediatorLiveData.setValue(detailRestaurantViewState);
    }
}
