package com.example.go4lunch.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreLikedRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.ui.detailrestaurant.viewmodel.DetailRestaurantViewModel;
import com.example.go4lunch.ui.detailrestaurant.viewmodel.DetailRestaurantViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private volatile static LoginViewModelFactory sInstance;

    @NonNull
    private final FirestoreUsersRepository firestoreUsersRepository;

    public LoginViewModelFactory(@NonNull FirestoreUsersRepository firestoreUsersRepository) {
        this.firestoreUsersRepository = firestoreUsersRepository;
    }

    public static LoginViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (LoginViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new LoginViewModelFactory(new FirestoreUsersRepository());
                }
            }
        }

        return sInstance;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(firestoreUsersRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}
