package com.example.go4lunch.ui.detailrestaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.repository.GooglePlacesApiRepository;

public class DetailRestaurantViewModelFactory implements ViewModelProvider.Factory{
    private volatile static DetailRestaurantViewModelFactory sInstance;

    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;
    private final String currentId;
    private final String currentPlaceId;

    public static DetailRestaurantViewModelFactory getInstance(String currentId, String currentPlaceId) {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (DetailRestaurantViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new DetailRestaurantViewModelFactory(
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey()),
                            currentId,
                            currentPlaceId);
                }
            }
        }

        return sInstance;
    }

    private DetailRestaurantViewModelFactory(
            @NonNull GooglePlacesApiRepository googlePlacesApiRepository,
            @NonNull String currentId,
            @NonNull String currentPlaceId) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        this.currentId = currentId;
        this.currentPlaceId = currentPlaceId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(googlePlacesApiRepository, currentId, currentPlaceId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }

}
