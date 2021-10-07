package com.example.gtlabgo4lunch.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.gtlabgo4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.gtlabgo4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.gtlabgo4lunch.ui.main.model.SimpleRestaurant;

import java.util.ArrayList;
import java.util.List;

public class GetPlaceDetailsByPlaceIds {
    private GooglePlacesApiRepository googlePlacesApiRepository;

    public GetPlaceDetailsByPlaceIds(GooglePlacesApiRepository googlePlacesApiRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    public LiveData<List<SimpleRestaurant>> get(List<String> placeIds) {
        MediatorLiveData<List<SimpleRestaurant>> simpleRestaurantMediatorLiveData = new MediatorLiveData<>();
        List<SimpleRestaurant> simpleRestaurants = new ArrayList<>();

        if (placeIds.size() == 0) {
            simpleRestaurantMediatorLiveData.setValue(simpleRestaurants);
        } else {
            for (String placeId : placeIds) {
                LiveData<PlaceDetails> source = googlePlacesApiRepository.getPaceDetails(placeId);
                simpleRestaurantMediatorLiveData.addSource(source, new Observer<PlaceDetails>() {
                    @Override
                    public void onChanged(PlaceDetails placeDetails) {
                        simpleRestaurantMediatorLiveData.removeSource(source);
                        simpleRestaurants.add(new SimpleRestaurant(
                                placeDetails.getResult().getPlaceId(),
                                placeDetails.getResult().getName()));
                        simpleRestaurantMediatorLiveData.setValue(simpleRestaurants);
                    }
                });
            }
        }
        return simpleRestaurantMediatorLiveData;
    }
}
