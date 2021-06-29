package com.example.go4lunch.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {
    private FirestoreUsersRepository firestoreUsersRepository = new FirestoreUsersRepository();

    public LiveData<String> getErrorLiveData() { return firestoreUsersRepository.getErrorLiveData(); }
    public LiveData<Boolean> getCreatedUserWithSuccessLiveData() {return  firestoreUsersRepository.getCreatedUserWithSuccessLiveData();}

    public LoginViewModel() {
    }

    public void addCurrentUserInFirestore(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){

            String urlPicture = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String uid = currentUser.getUid();

            firestoreUsersRepository.createUser(uid, userName, userEmail, urlPicture);
        }
    }


}
