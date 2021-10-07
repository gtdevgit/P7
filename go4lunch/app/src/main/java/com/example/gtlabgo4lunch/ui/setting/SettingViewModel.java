package com.example.gtlabgo4lunch.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.gtlabgo4lunch.data.applicationsettings.SettingRepository;

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
            settingRepository.disallowAuthorisationNotification();
        }

    }

}