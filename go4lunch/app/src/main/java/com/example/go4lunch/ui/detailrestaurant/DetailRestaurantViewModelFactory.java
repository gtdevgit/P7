package com.example.go4lunch.ui.detailrestaurant;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;

public class DetailRestaurantViewModelFactory implements ViewModelProvider.Factory{
    private volatile static DetailRestaurantViewModelFactory sInstance;

    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;
    private final String currentId;

    public static DetailRestaurantViewModelFactory getInstance(String currentId) {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (DetailRestaurantViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new DetailRestaurantViewModelFactory(
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey()),
                            currentId);
                }
            }
        }

        return sInstance;
    }

    private DetailRestaurantViewModelFactory(
            @NonNull GooglePlacesApiRepository googlePlacesApiRepository,
            @NonNull String currentId) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        this.currentId = currentId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(googlePlacesApiRepository, currentId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }

}
