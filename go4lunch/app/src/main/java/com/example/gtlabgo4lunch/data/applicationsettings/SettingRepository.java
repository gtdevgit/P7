package com.example.gtlabgo4lunch.data.applicationsettings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gtlabgo4lunch.MainApplication;

public class SettingRepository {
    private final String PREFERENCE_FILE_NAME = "go4lunch_prefrence";
    private final String NOTIFICATION_KEY = "allow_notification";

    private SharedPreferences sharedPreferences = MainApplication.getApplication().getSharedPreferences(PREFERENCE_FILE_NAME,
            Context.MODE_PRIVATE);
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    private MutableLiveData<Boolean> allowNotificationMutableLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getAllowNotificationLiveData() {
        return allowNotificationMutableLiveData;
    }

    public SettingRepository() {
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(NOTIFICATION_KEY)) {
                    boolean value = getAuthorisationNotification();
                    allowNotificationMutableLiveData.setValue(new Boolean(value));
                }
            }
        };
    }

    private void setAuthorisationNotification(Boolean allow){
        sharedPreferences.edit()
                .putBoolean(NOTIFICATION_KEY, allow)
                .apply();
    }

    public boolean getAuthorisationNotification(){
        return sharedPreferences.getBoolean(NOTIFICATION_KEY, false);
    }

    public void allowAuthorisationNotification(){
        setAuthorisationNotification(true);
    }

    public void disallowAuthorisationNotification(){
        setAuthorisationNotification(false);
    }

    public void load(){
        boolean value = getAuthorisationNotification();
        allowNotificationMutableLiveData.setValue(new Boolean(value));
    }

    private void registerOnSharedPreferenceChangeListener(){
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void unregisterOnSharedPreferenceChangeListener(){
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}
