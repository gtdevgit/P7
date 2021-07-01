package com.example.go4lunch.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.data.location.LocationRepository;
import com.example.go4lunch.data.permission_checker.PermissionChecker;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.google.android.gms.location.LocationServices;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private volatile static MainViewModelFactory sInstance;

    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;
    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;

    public static MainViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (MainViewModelFactory.class) {
                if (sInstance == null) {
                    Application application = MainApplication.getApplication();

                    sInstance = new MainViewModelFactory(
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey()),
                            new PermissionChecker(application),
                            new LocationRepository(LocationServices.getFusedLocationProviderClient(application)));
                }
            }
        }

        return sInstance;
    }

    private MainViewModelFactory(
            @NonNull GooglePlacesApiRepository googlePlacesApiRepository,
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository) {
        this.googlePlacesApiRepository = googlePlacesApiRepository;
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(googlePlacesApiRepository, permissionChecker, locationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}
