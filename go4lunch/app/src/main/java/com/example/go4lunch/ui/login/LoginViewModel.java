package com.example.go4lunch.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
