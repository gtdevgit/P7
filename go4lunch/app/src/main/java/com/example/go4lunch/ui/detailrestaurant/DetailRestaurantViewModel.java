package com.example.go4lunch.ui.detailrestaurant;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.DetailRestaurant;
import com.example.go4lunch.models.googleplaces.Photo;
import com.example.go4lunch.models.googleplaces.palcesdetails.PlaceDetails;
import com.example.go4lunch.models.googleplaces.placesearch.Result;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.google.gson.JsonObject;

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

    public DetailRestaurantViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    public LiveData<DetailRestaurant> getDetailRestaurantMutableLiveData() {
        return detailRestaurantMutableLiveData;
    }

    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
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
                    for (Photo photo : placeDetails.getResult().getPhotos()){
                        if ((photo.getPhotoReference() != null) && (photo.getPhotoReference().trim().length() > 0)) {
                            urlPhotos.add(googlePlacesApiRepository.getUrlPlacePhoto(photo.getPhotoReference()));
                        }
                    }
                    String urlPicture = (urlPhotos.size() > 0) ? urlPhotos.get(0) : null;
                    double rating = placeDetails.getResult().getRating();
                    boolean haveStar1 = false;
                    boolean haveStar2 = false;
                    boolean haveStar3 = false;
                    boolean isLiked = false;
                    boolean isOpen = placeDetails.getResult().getOpeningHours().getOpenNow();
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
}
