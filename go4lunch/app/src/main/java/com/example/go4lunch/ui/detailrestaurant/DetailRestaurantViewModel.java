package com.example.go4lunch.ui.detailrestaurant;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.api.firestore.ChoosenHelper;
import com.example.go4lunch.api.firestore.ChoosenHelperListener;
import com.example.go4lunch.api.firestore.LikeHelper;
import com.example.go4lunch.api.firestore.LikeHelperListener;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.api.firestore.UserHelperListener;
import com.example.go4lunch.models.DetailRestaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.models.googleplaces.Photo;
import com.example.go4lunch.models.googleplaces.palcesdetails.PlaceDetails;
import com.example.go4lunch.models.googleplaces.placesearch.Result;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRestaurantViewModel extends ViewModel {

    private GooglePlacesApiRepository googlePlacesApiRepository;

    private final MutableLiveData<DetailRestaurant> detailRestaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final MutableLiveData<Boolean> likedMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<Boolean> choosenMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<List<User>> workmatesMutableLiveData = new MutableLiveData<>();

    private final ChoosenHelperListener choosenHelperListener;
    private final UserHelperListener userHelperListener;

    public DetailRestaurantViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        // listener for user
        this.userHelperListener = new UserHelperListener() {
            @Override
            public void onGetUser(User user) {

            }

            @Override
            public void onGetUsersByList(List<User> users) {
                workmatesMutableLiveData.postValue(users);
            }

            @Override
            public void onErrorMessage(String message) {
                errorMutableLiveData.postValue(message);
            }
        };

        // listener for liked and choosen restaurants
        this.choosenHelperListener = new ChoosenHelperListener() {
            @Override
            public void onGetChoosen(boolean isChoosen) {
                choosenMutableLiveData.postValue(Boolean.valueOf(isChoosen));
            }

            @Override
            public void onFailure(Exception e) {
                errorMutableLiveData.postValue(e.getMessage());
            }

            @Override
            public void onGetUsersWhoChoseThisRestaurant(List<UserRestaurantAssociation> userRestaurantAssociationList) {
                Log.d(Tag.TAG, "DetailRestaurantViewModel.onGetUsersWhoChoseThisRestaurant() called with: userRestaurantAssociationList = [" + userRestaurantAssociationList + "]");
                List<String> uidList = userRestaurantAssociationListToUidList(userRestaurantAssociationList);

                UserHelper.getUsersByList(uidList, userHelperListener);
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

    public LiveData<Boolean> getChoosenLiveData() {
        return choosenMutableLiveData;
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return workmatesMutableLiveData;
    }

    /**
     * transforms one List<UserRestaurantAssociation> to one List<String> containing each uid
     * @param userRestaurantAssociationList
     * @return
     */
    private List<String> userRestaurantAssociationListToUidList(List<UserRestaurantAssociation> userRestaurantAssociationList){
        List<String> uidList = new ArrayList<>();
        for (UserRestaurantAssociation userRestaurantAssociation : userRestaurantAssociationList) {
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
                            urlPhotos);
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
    public void loadIsChoosen(String uid, String placeId){
        ChoosenHelper.isChoosenRestaurant(uid, placeId, this.choosenHelperListener);
    }

    /**
     * user choose restaurant
     * @param uid
     * @param placeId
     */
    public void choose(String uid, String placeId){
        ChoosenHelper.createChoosenRestaurant(uid, placeId, this.choosenHelperListener);
    }

    /**
     * user unchoose restaurant
     * @param uid
     * @param placeId
     */
    public void unchoose(String uid, String placeId) {
        ChoosenHelper.deleteChoosenRestaurant(uid, placeId, this.choosenHelperListener);
    }

    public void loadWorkmates(String placeId){
        ChoosenHelper.getUsersWhoChoseThisRestaurant(placeId, this.choosenHelperListener);
    }

}
