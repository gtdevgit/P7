package com.example.gtlabgo4lunch.ui.logout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gtlabgo4lunch.MainApplication;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.gtlabgo4lunch.tag.Tag;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogoutViewModel extends ViewModel {

    private static final String TAG = Tag.TAG;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private ListenerLogoutUser listenerLogoutUser;

    FirestoreUsersRepository firestoreUsersRepository;
    public LiveData<String> getErrorLiveData() { return firestoreUsersRepository.getErrorLiveData(); }
    public LiveData<Boolean> getDeletedUserWithSuccessLiveData() {return  firestoreUsersRepository.getDeletedUserWithSuccessLiveData();}

    private MutableLiveData<LogoutViewState> logoutViewStateMutableLiveData = new MutableLiveData<>();
    public LiveData<LogoutViewState> getLogoutViewStateLiveData() {
        return logoutViewStateMutableLiveData;
    }

    public LogoutViewModel(FirestoreUsersRepository firestoreUsersRepository) {
        this.firestoreUsersRepository = firestoreUsersRepository;
    }

    public void setListenerLogoutUser(ListenerLogoutUser listenerLogoutUser) {
        this.listenerLogoutUser = listenerLogoutUser;
    }

    public void loadData() {
        Log.d(TAG, "loadData() called");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Log.d(TAG, "loadData() current user == null");
            // use postValue instead of setValue to avoid error
            // "IllegalStateException: Cannot invoke setValue on a background thread"
            // when logout callback
            logoutViewStateMutableLiveData.postValue(new LogoutViewState(
                    MainApplication.getApplication().getString(R.string.no_user_email),
                    MainApplication.getApplication().getString(R.string.no_user_name_found),
                    Uri.EMPTY,
                    false,
                    false));
        } else {
            //Get email & username from Firebase
            Log.d(TAG, "loadData() current user = " + currentUser);
            logoutViewStateMutableLiveData.setValue(new LogoutViewState(
                    currentUser.getEmail(),
                    currentUser.getDisplayName(),
                    currentUser.getPhotoUrl(),
                    true,
                    true));
        }
    }

    public void signOutUserFromFirebase(Context context){
        Executor executor = Executors.newSingleThreadExecutor();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AuthUI.getInstance()
                    .signOut(context)
                    .addOnSuccessListener(executor, updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
        }
    }

    public void deleteUserFromFirebase(Context context){
        Log.d(TAG, "deleteUserFromFirebase() called with: context = [" + context + "]");
        Executor executor = Executors.newSingleThreadExecutor();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firestoreUsersRepository.deleteUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
            AuthUI.getInstance()
                    .delete(context)
                    .addOnSuccessListener(executor, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted() called with: origin = [" + origin + "]");
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                    case DELETE_USER_TASK:
                        loadData();
                        listenerLogoutUser.onSuccess();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}