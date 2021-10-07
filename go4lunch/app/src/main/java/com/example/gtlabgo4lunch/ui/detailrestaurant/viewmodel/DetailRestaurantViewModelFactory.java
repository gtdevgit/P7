package com.example.gtlabgo4lunch.ui.detailrestaurant.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gtlabgo4lunch.MainApplication;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.gtlabgo4lunch.data.googleplace.repository.GooglePlacesApiRepository;

public class DetailRestaurantViewModelFactory implements ViewModelProvider.Factory{

    private volatile static DetailRestaurantViewModelFactory sInstance;

    @NonNull
    private final String currentId;
    @NonNull
    private final FirestoreChosenRepository firestoreChosenRepository;
    @NonNull
    private final FirestoreLikedRepository firestoreLikedRepository;
    @NonNull
    private final FirestoreUsersRepository firestoreUsersRepository;
    @NonNull
    private final GooglePlacesApiRepository googlePlacesApiRepository;

    public static DetailRestaurantViewModelFactory getInstance(String currentId) {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (DetailRestaurantViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new DetailRestaurantViewModelFactory(
                            currentId,
                            new FirestoreChosenRepository(),
                            new FirestoreLikedRepository(),
                            new FirestoreUsersRepository(),
                            new GooglePlacesApiRepository(MainApplication.getGoogleApiKey()));
                }
            }
        }

        return sInstance;
    }

    private DetailRestaurantViewModelFactory(
            @NonNull String currentId,
            @NonNull FirestoreChosenRepository firestoreChosenRepository,
            @NonNull FirestoreLikedRepository firestoreLikedRepository,
            @NonNull FirestoreUsersRepository firestoreUsersRepository,
            @NonNull GooglePlacesApiRepository googlePlacesApiRepository) {
        this.currentId = currentId;
        this.firestoreChosenRepository = firestoreChosenRepository;
        this.firestoreLikedRepository = firestoreLikedRepository;
        this.firestoreUsersRepository = firestoreUsersRepository;
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailRestaurantViewModel.class)) {
            return (T) new DetailRestaurantViewModel(
                    currentId,
                    firestoreChosenRepository,
                    firestoreLikedRepository,
                    firestoreUsersRepository,
                    googlePlacesApiRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }

}
