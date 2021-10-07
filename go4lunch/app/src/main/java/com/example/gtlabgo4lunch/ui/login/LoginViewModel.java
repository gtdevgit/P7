package com.example.gtlabgo4lunch.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;

public class LoginViewModel extends ViewModel {
    private FirestoreUsersRepository firestoreUsersRepository;

    public LiveData<String> getErrorLiveData() { return firestoreUsersRepository.getErrorLiveData(); }
    public LiveData<Boolean> getCreatedUserWithSuccessLiveData() {return  firestoreUsersRepository.getCreatedUserWithSuccessLiveData();}

    public LoginViewModel(FirestoreUsersRepository firestoreUsersRepository) {
        this.firestoreUsersRepository = firestoreUsersRepository;
    }

    public void addCurrentUserInFirestore(String uid, String userName, String userEmail, String urlPicture){
        firestoreUsersRepository.createUser(uid, userName, userEmail, urlPicture);
    }
}
