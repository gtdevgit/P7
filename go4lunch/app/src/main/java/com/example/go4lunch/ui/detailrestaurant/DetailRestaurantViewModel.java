package com.example.go4lunch.ui.detailrestaurant;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.api.firestore.ChosenHelper;
import com.example.go4lunch.api.firestore.ChosenListener;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.LikeHelper;
import com.example.go4lunch.api.firestore.LikeHelperListener;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.api.firestore.UserListListener;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.models.googleplaces.palcesdetails.Result;
import com.example.go4lunch.ui.model.DetailRestaurantViewState;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.models.googleplaces.Photo;
import com.example.go4lunch.models.googleplaces.palcesdetails.PlaceDetails;
import com.example.go4lunch.ui.model.SimpleUserViewState;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRestaurantViewModel extends ViewModel {

    private final int STAR_LEVEL_1 = 1;
    private final int STAR_LEVEL_2 = 2;
    private final int STAR_LEVEL_3 = 3;

    private GooglePlacesApiRepository googlePlacesApiRepository;

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<Boolean> likedMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<List<SimpleUserViewState>> workmatesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> countLikedMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<Integer> star1ColorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> star2ColorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> star3ColorMutableLiveData = new MutableLiveData<>();

    private ListenerRegistration registrationChosen;
    private ListenerRegistration registrationLiked;

    private final FailureListener failureListener;
    private final UserRestaurantAssociationListListener userRestaurantAssociationListListener;
    private final UserListListener userListListener;

    private FirestoreUsersRepository firestoreUsersRepository;
    private FirestoreChosenRepository firestoreChosenRepository;
    private FirestoreLikedRepository firestoreLikedRepository;
    private final MediatorLiveData<DetailRestaurantViewState> detailRestaurantViewStateMediatorLiveData = new MediatorLiveData<>();

    public LiveData<DetailRestaurantViewState> getDetailRestaurantViewStateLiveData() {
        return detailRestaurantViewStateMediatorLiveData;
    }

    // for combine
    private PlaceDetails placeDetails;
    private List<UidPlaceIdAssociation> likedRestaurantsByPlaceId;
    private List<UidPlaceIdAssociation> chosenRestaurantsByPlaceId;
    private List<SimpleUserViewState> workmatesByPlaceId;

    // cache
    private CacheDetailRestaurantViewModel cache;

    public DetailRestaurantViewModel(GooglePlacesApiRepository googlePlacesApiRepository,
                                     String currentUid,
                                     String currentPlaceId ) {

        this.googlePlacesApiRepository = googlePlacesApiRepository;
        cache = new CacheDetailRestaurantViewModel(currentUid, currentPlaceId);

        firestoreUsersRepository = new FirestoreUsersRepository();
        firestoreChosenRepository = new FirestoreChosenRepository();
        firestoreLikedRepository = new FirestoreLikedRepository();

        configureMediatorLiveData();

        this.failureListener = new FailureListener() {
            @Override
            public void onFailure(Exception e) {
                errorMutableLiveData.postValue(e.getMessage());
            }
        };

        this.userListListener = new UserListListener() {
            @Override
            public void onGetUsers(List<User> users) {
                workmatesMutableLiveData.postValue(usersListToSimpleUserViewStateList(users));
            }
        };

        this.userRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                Log.d(Tag.TAG, "DetailRestaurantViewModel.onGetUsersWhoChoseThisRestaurant() called with: userRestaurantAssociations = [" + uidPlaceIdAssociations + "]");
                List<String> uidList = userRestaurantAssociationListToUidList(uidPlaceIdAssociations);

                UserHelper.getUsersByUidList(uidList, userListListener, failureListener);
            }
        };
    }

    public void load(String placeId, String uid){
        loadDetailRestaurant(placeId);
        //loadIsLikedByUid(uid, placeId);
        //loadIsChosen(uid, placeId);
        loadWorkmatesByPlaceId(placeId);
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
                // tranforme to List Uid
                List<String> uids = new ArrayList<>();
                for (UidPlaceIdAssociation association : uidPlaceIdAssociations){
                    uids.add(association.getUserUid());
                }
                // we have uid, now we need name en url picture
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

    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    public LiveData<Boolean> getLikedLiveData() {
        return likedMutableLiveData;
    }

    public LiveData<List<SimpleUserViewState>> getWorkmatesLiveData() {
        return workmatesMutableLiveData;
    }

    public LiveData<Integer> getCountLikedLiveData() {
        return countLikedMutableLiveData;
    }

    public LiveData<Integer> getStar1ColorMutableLiveData() {
        return star1ColorMutableLiveData;
    }

    public LiveData<Integer> getStar2ColorMutableLiveData() {
        return star2ColorMutableLiveData;
    }

    public LiveData<Integer> getStar3ColorMutableLiveData() {
        return star3ColorMutableLiveData;
    }

    /**
     * transforms one List<UserRestaurantAssociation> to one List<String> containing each uid
     * @param uidPlaceIdAssociations
     * @return
     */
    private List<String> userRestaurantAssociationListToUidList(List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        List<String> uidList = new ArrayList<>();
        for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations) {
            uidList.add(uidPlaceIdAssociation.getUserUid());
        }
        return uidList;
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

    public void loadDetailRestaurant(String placeId) {
        Log.d(Tag.TAG, "loadDetailRestaurant() called with: placeId = [" + placeId + "]");
        firestoreChosenRepository.loadChosenRestaurantsByPlaceId(placeId);
        firestoreLikedRepository.loadLikedRestaurantsByPlaceId(placeId);
        googlePlacesApiRepository.loadDetails(placeId);
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

    public void loadIsLikedByUid(String uid, String placeId){
        LikeHelper.isLiked(uid, placeId, new LikeHelperListener() {
            @Override
            public void onGetLike(boolean isLiked) {
                likedMutableLiveData.postValue(Boolean.valueOf(isLiked));
            }
        });
    }

    public void like(String uid, String placeId){
        LikeHelper.createLike(uid, placeId, new LikeHelperListener() {
            @Override
            public void onGetLike(boolean isLiked) {
                likedMutableLiveData.postValue(Boolean.valueOf(isLiked));
            }
        });
    }

    public void unlike(String uid, String placeId) {
        LikeHelper.deleteLike(uid, placeId, new LikeHelperListener() {
            @Override
            public void onGetLike(boolean isLiked) {
                likedMutableLiveData.postValue(Boolean.valueOf(isLiked));
            }
        });
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

    public void loadWorkmatesByPlaceId(String placeId){
        //ChosenHelper.getUsersWhoChoseThisRestaurant(placeId, this.userRestaurantAssociationListListener, this.failureListener);
        firestoreChosenRepository.loadChosenRestaurantsByPlaceId(placeId);
    }

    /**
     * for real time workmates list
     * @param placeId
     */
    public void activateWormatesByPlaceListener(String placeId){
        Log.d(Tag.TAG, "DetailRestaurantViewModel.activateUsersListener() called");
        registrationChosen = ChosenHelper.getChosenCollection()
            .whereEqualTo("placeId", placeId)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value,
                                @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null){
                    errorMutableLiveData.postValue(error.getMessage());
                    return;
                }

                List<UidPlaceIdAssociation> uidPlaceIdAssociations = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    if (document.exists()) {
                        UidPlaceIdAssociation uidPlaceIdAssociation = document.toObject(UidPlaceIdAssociation.class);
                        uidPlaceIdAssociations.add(uidPlaceIdAssociation);
                    }
                }
                List<String> uidList = userRestaurantAssociationListToUidList(uidPlaceIdAssociations);
                UserHelper.getUsersByUidList(uidList, userListListener, failureListener);
            }
        });
    };

    public void removeWormatesByPlaceListener(){
        if (registrationChosen != null) {
            Log.d(Tag.TAG, "DetailRestaurantViewModel.removeUsersListener() called");
            registrationChosen.remove();
        }
    };

    /**
     * for real time workmates list
     * @param placeId
     */
    public void activateLikedByPlaceListener(String placeId){
        Log.d(Tag.TAG, "DetailRestaurantViewModel.activateUsersListener() called");
        registrationLiked = LikeHelper.getLikedCollection()
                .whereEqualTo("placeId", placeId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            errorMutableLiveData.postValue(error.getMessage());
                            return;
                        }
                        int likeCounter = value.size();
                        countLikedMutableLiveData.postValue(new Integer(likeCounter));
                        int star1Color = getStarColorByLevel(STAR_LEVEL_1, likeCounter);
                        star1ColorMutableLiveData.postValue(new Integer(star1Color));
                        int star2Color = getStarColorByLevel(STAR_LEVEL_2, likeCounter);
                        star2ColorMutableLiveData.postValue(new Integer(star2Color));
                        int star3Color = getStarColorByLevel(STAR_LEVEL_3, likeCounter);
                        star3ColorMutableLiveData.postValue(new Integer(star3Color));
                    }
                });
    };

    public void removeLikedByPlaceListener(){
        if (registrationLiked != null) {
            Log.d(Tag.TAG, "DetailRestaurantViewModel.removeUsersListener() called");
            registrationLiked.remove();
        }
    };

    private boolean isContainPlaceid(String placeId, List<UidPlaceIdAssociation> list){
        for (UidPlaceIdAssociation uidPlaceIdAssociation : list){
            if (placeId.equals(uidPlaceIdAssociation.getPlaceId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isContainUid(String uid, List<UidPlaceIdAssociation> list){
        for (UidPlaceIdAssociation uidPlaceIdAssociation : list){
            if (uid.equals(uidPlaceIdAssociation.getUserUid())) {
                return true;
            }
        }
        return false;
    }

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
