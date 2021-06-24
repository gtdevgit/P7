package com.example.go4lunch.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.tag.Tag;

import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<String>();
    private final LiveData<List<User>> workmatesLiveData;

    FirestoreUsersRepository firestoreUsersRepository;

    public WorkmatesViewModel() {
        firestoreUsersRepository = new FirestoreUsersRepository();
        workmatesLiveData = firestoreUsersRepository.getUsersLiveData();
    }

    public LiveData<List<User>> getWorkmatesLiveData() {
        return workmatesLiveData;
    }

    public LiveData<String> getErrorLiveData(){
        return this.errorMutableLiveData;
    }

    public void loadWorkmates(){
        firestoreUsersRepository.loadAllUsers();
    }

    /**
     * to get real time change in users list
     */
    public void activateWorkmatesListener(){
        Log.d(Tag.TAG, "WorkmatesViewModel.activateUsersListener() called");
        firestoreUsersRepository.activeRealTimeListener();
    }

    public void removeWorkmatesListener(){
        firestoreUsersRepository.removeRealTimeListener();
    }
}
