package com.example.gtlabgo4lunch.ui.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gtlabgo4lunch.MainApplication;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.gtlabgo4lunch.data.location.LocationRepository;
import com.example.gtlabgo4lunch.data.permission_checker.PermissionChecker;
import com.example.gtlabgo4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.google.android.gms.location.LocationServices;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private volatile static MainViewModelFactory sInstance;

    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;
    @NonNull
    private final FirestoreChosenRepository firestoreChosenRepository;
    @NonNull
    private final FirestoreLikedRepository firestoreLikedRepository;
    @NonNull
    private final FirestoreUsersRepository firestoreUsersRepository;
    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;

    public static MainViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (MainViewModelFactory.class) {
                if (sInstance == null) {
                    Application application = MainApplication.getApplication();

                    sInstance = new MainViewModelFactory(
                            new PermissionChecker(application),
                            new LocationRepository(LocationServices.getFusedLocationProviderClient(application)),
                            new FirestoreChosenRepository(),
                            new FirestoreLikedRepository(),
                            new FirestoreUsersRepository(),
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey()));
                }
            }
        }

        return sInstance;
    }

    private MainViewModelFactory(
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository,
            @NonNull FirestoreChosenRepository firestoreChosenRepository,
            @NonNull FirestoreLikedRepository firestoreLikedRepository,
            @NonNull FirestoreUsersRepository firestoreUsersRepository,
            @NonNull GooglePlacesApiRepository googlePlacesApiRepository) {
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
        this.firestoreChosenRepository = firestoreChosenRepository;
        this.firestoreLikedRepository = firestoreLikedRepository;
        this.firestoreUsersRepository = firestoreUsersRepository;
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(permissionChecker,
                    locationRepository,
                    firestoreChosenRepository,
                    firestoreLikedRepository,
                    firestoreUsersRepository,
                    googlePlacesApiRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}
