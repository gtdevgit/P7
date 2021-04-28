package com.example.go4lunch.ui.setting;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.PermissionActivity;

public class SettingViewModel extends ViewModel {

    private MutableLiveData<String> permissionLocalization;

    public SettingViewModel() {
        permissionLocalization = new MutableLiveData<>();
    }

    public LiveData<String> getPermissionLocalization() {
        return permissionLocalization;
    }
}