package com.example.go4lunch.ui.detailrestaurant;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.ChosenHelper;
import com.example.go4lunch.api.firestore.ChosenListener;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.LikeHelper;
import com.example.go4lunch.api.firestore.LikeHelperListener;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.api.firestore.UserListListener;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.models.DetailRestaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.models.googleplaces.Photo;
import com.example.go4lunch.models.googleplaces.palcesdetails.PlaceDetails;
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

    private GooglePlacesApiRepository googlePlacesApiRepository;

    private final MutableLiveData<DetailRestaurant> detailRestaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<Boolean> likedMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<Boolean> chosenMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<List<User>> workmatesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> countLikedMutableLiveData = new MutableLiveData<>();

    private ListenerRegistration registrationChosen;
    private ListenerRegistration registrationLiked;

    private final ChosenListener chosenListener;
    private final FailureListener failureListener;
    private final UserRestaurantAssociationListListener userRestaurantAssociationListListener;
    private final UserListListener userListListener;

    public DetailRestaurantViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;

        this.chosenListener = new ChosenListener() {
            @Override
            public void onGetChosen(boolean isChosen) {
                chosenMutableLiveData.postValue(Boolean.valueOf(isChosen));
            }
        };

        this.failureListener = new FailureListener() {
            @Override
            public void onFailure(Exception e) {
                errorMutableLiveData.postValue(e.getMessage());
            }
        };

        this.userListListener = new UserListListener() {
            @Override
            public void onGetUsers(List<User> users) {
                workmatesMutableLiveData.postValue(users);
            }
        };

        this.userRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations) {
                Log.d(Tag.TAG, "DetailRestaurantViewModel.onGetUsersWhoChoseThisRestaurant() called with: userRestaurantAssociations = [" + userRestaurantAssociations + "]");
                List<String> uidList = userRestaurantAssociationListToUidList(userRestaurantAssociations);

                UserHelper.getUsersByUidList(uidList, userListListener, failureListener);
            }
        };
    }

    public LiveData<DetailRestaurant> getDetailRestaurantLiveData() {
        return detailRestaurantMutableLiveData;
    }

    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    public LiveData<Boolean> getLikedLiveData() {
        return likedMutableLiveData;
    }

    public LiveData<Boolean> getChosenLiveData() {
        return chosenMutableLiveData;
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return workmatesMutableLiveData;
    }

    public LiveData<Integer> getCountLikedLiveData() {
        return countLikedMutableLiveData;
    }

    /**
     * transforms one List<UserRestaurantAssociation> to one List<String> containing each uid
     * @param userRestaurantAssociations
     * @return
     */
    private List<String> userRestaurantAssociationListToUidList(List<UserRestaurantAssociation> userRestaurantAssociations){
        List<String> uidList = new ArrayList<>();
        for (UserRestaurantAssociation userRestaurantAssociation : userRestaurantAssociations) {
            uidList.add(userRestaurantAssociation.getUserUid());
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
        List<UserRestaurantAssociation> likedUserRestaurants = new ArrayList<>();

        UserRestaurantAssociationListListener userRestaurantAssociationListListener = new UserRestaurantAssociationListListener() {
            @Override
            public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations) {
                likedUserRestaurants.addAll(userRestaurantAssociations);

                Call<PlaceDetails> call = googlePlacesApiRepository.getDetails(placeId);
                call.enqueue(new Callback<PlaceDetails>() {
                    @Override
                    public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                        Log.d(Tag.TAG, "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                        if (response.isSuccessful()) {
                            PlaceDetails placeDetails = response.body();
                            String placeId = placeDetails.getResult().getPlaceId();
                            String name = placeDetails.getResult().getName();
                            String info = findAddress(placeDetails.getResult().getFormattedAddress(), placeDetails.getResult().getVicinity());
                            String phoneNumber = placeDetails.getResult().getInternationalPhoneNumber();
                            String website = placeDetails.getResult().getWebsite();
                            List<String> urlPhotos = new ArrayList<>();
                            if (placeDetails.getResult().getPhotos() != null) {
                                for (Photo photo : placeDetails.getResult().getPhotos()){
                                    if ((photo.getPhotoReference() != null) && (photo.getPhotoReference().trim().length() > 0)) {
                                        urlPhotos.add(googlePlacesApiRepository.getUrlPlacePhoto(photo.getPhotoReference()));
                                    }
                                }
                            }
                            String urlPicture = (urlPhotos.size() > 0) ? urlPhotos.get(0) : null;

                            double rating = placeDetails.getResult().getRating();
                            boolean haveStar1 = false;
                            boolean haveStar2 = false;
                            boolean haveStar3 = false;
                            boolean isLiked = false;
                            boolean isOpen = (placeDetails.getResult().getOpeningHours() == null) ? false : placeDetails.getResult().getOpeningHours().getOpenNow();
                            List<String> workmates = null;
                            int countLike = likedUserRestaurants.size();
                            DetailRestaurant detailRestaurant = new DetailRestaurant(placeId,
                                    name,
                                    info,
                                    phoneNumber,
                                    website,
                                    urlPicture,
                                    rating,
                                    haveStar1,
                                    haveStar2,
                                    haveStar3,
                                    isLiked,
                                    isOpen,
                                    workmates,
                                    urlPhotos,
                                    countLike);
                            detailRestaurantMutableLiveData.postValue(detailRestaurant);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDetails> call, Throwable t) {
                        Log.d(Tag.TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                        errorMutableLiveData.postValue(t.getMessage());
                    }
                });
            }
        };

        LikeHelper.getUsersWhoLikedThisRestaurant(placeId, userRestaurantAssociationListListener, failureListener);
    }

    public void loadIsLiked(String uid, String placeId){
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

    /**
     * is restaurant choose
     * @param uid
     * @param placeId
     */
    public void loadIsChosen(String uid, String placeId){
        ChosenHelper.isChosenRestaurant(uid, placeId, this.chosenListener, this.failureListener);
    }

    /**
     * user choose restaurant
     * @param uid
     * @param placeId
     */
    public void choose(String uid, String placeId){
        ChosenHelper.createChosenRestaurant(uid, placeId, this.chosenListener, this.failureListener);
    }

    /**
     * user unchoose restaurant
     * @param uid
     * @param placeId
     */
    public void unchoose(String uid, String placeId) {
        ChosenHelper.deleteChosenRestaurant(uid, placeId, this.chosenListener, this.failureListener);
    }

    public void loadWorkmatesByPlace(String placeId){
        ChosenHelper.getUsersWhoChoseThisRestaurant(placeId, this.userRestaurantAssociationListListener, this.failureListener);
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

                List<UserRestaurantAssociation> userRestaurantAssociations = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    if (document.exists()) {
                        UserRestaurantAssociation userRestaurantAssociation = document.toObject(UserRestaurantAssociation.class);
                        userRestaurantAssociations.add(userRestaurantAssociation);
                    }
                }
                List<String> uidList = userRestaurantAssociationListToUidList(userRestaurantAssociations);
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
                        int countLike = value.size();
                        countLikedMutableLiveData.postValue(new Integer(countLike));
                    }
                });
    };

    public void removeLikedByPlaceListener(){
        if (registrationLiked != null) {
            Log.d(Tag.TAG, "DetailRestaurantViewModel.removeUsersListener() called");
            registrationLiked.remove();
        }
    };

}
