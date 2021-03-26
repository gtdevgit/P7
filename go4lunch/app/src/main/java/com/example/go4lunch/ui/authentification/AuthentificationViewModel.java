package com.example.go4lunch.ui.authentification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthentificationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AuthentificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is authentification fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
