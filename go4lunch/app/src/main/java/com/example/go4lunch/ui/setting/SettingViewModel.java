package com.example.go4lunch.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SettingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is your settings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}