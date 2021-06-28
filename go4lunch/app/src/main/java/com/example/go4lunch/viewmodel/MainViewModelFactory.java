package com.example.go4lunch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.google.android.gms.location.LocationServices;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private volatile static MainViewModelFactory sInstance;

    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;

    public static MainViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (MainViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new MainViewModelFactory(
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey())
                    );
                }
            }
        }

        return sInstance;
    }

    private MainViewModelFactory(@NonNull GooglePlacesApiRepository googlePlacesApiRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(googlePlacesApiRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}
