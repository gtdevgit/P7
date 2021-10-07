package com.example.gtlabgo4lunch.ui.logout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;

import org.jetbrains.annotations.NotNull;

public class LogoutViewModelFactory implements ViewModelProvider.Factory {

    private volatile static LogoutViewModelFactory sInstance;

    @NonNull
    private final FirestoreUsersRepository firestoreUsersRepository;

    public LogoutViewModelFactory(@NonNull FirestoreUsersRepository firestoreUsersRepository) {
        this.firestoreUsersRepository = firestoreUsersRepository;
    }

    public static LogoutViewModelFactory getInstance() {
        if (sInstance == null) {
            // Double Checked Locking singleton pattern with Volatile works on Android since Honeycomb
            synchronized (LogoutViewModelFactory.class) {
                if (sInstance == null) {
                    //Application application = MainApplication.getApplication();
                    sInstance = new LogoutViewModelFactory(new FirestoreUsersRepository());
                }
            }
        }

        return sInstance;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LogoutViewModel.class)) {
            return (T) new LogoutViewModel(firestoreUsersRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class : " + modelClass);
    }
}
