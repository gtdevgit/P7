package com.example.go4lunch.ui.setting;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.PermissionActivity;
import com.example.go4lunch.data.applicationsettings.SettingRepository;

public class SettingViewModel extends ViewModel {


    private SettingRepository settingRepository = new SettingRepository();

    public LiveData<Boolean> getAllowNotificationLiveData(){
        return settingRepository.getAllowNotificationLiveData();
    }

    public SettingViewModel() {
    }

    public void load(){
        settingRepository.load();
    }

    public void changeAutorisationNotification(boolean isChecked){
        if (isChecked){
            settingRepository.allowAuthorisationNotification();
        } else {
            settingRepository.diallowAuthorisationNotification();
        }

    }

}