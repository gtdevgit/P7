package com.example.go4lunch.ui.setting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.PermissionActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.notification.NotificationHelper;

public class SettingFragment extends Fragment {

    private TextView textViewPermissionLocalization;
    private CheckBox checkBoxAuthoriseNotification;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private SettingViewModel settingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        configureComponents(root);
        configureViewModel();

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            textViewPermissionLocalization.setText(R.string.localisation_allowed);
        } else {
            textViewPermissionLocalization.setText(R.string.localisation_not_allowed);}

        return root;
    }

    private void configureComponents(View root){
        textViewPermissionLocalization = root.findViewById(R.id.fragment_setting_textview_permission_localisation);
        checkBoxAuthoriseNotification = root.findViewById(R.id.fragment_setting_checkbox_show_notification);

        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingViewModel.changeAutorisationNotification(isChecked);
                if (isChecked){
                    NotificationHelper.startNotificationWorker(getContext());
                } else {
                    NotificationHelper.stopNotificationWorker(getContext());
                }
            }
        };
        checkBoxAuthoriseNotification.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void configureViewModel(){
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        settingViewModel.getAllowNotificationLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                checkBoxAuthoriseNotification.setOnCheckedChangeListener(null);
                checkBoxAuthoriseNotification.setChecked(aBoolean);
                checkBoxAuthoriseNotification.setOnCheckedChangeListener(onCheckedChangeListener);
            }
        });
        settingViewModel.load();
    }
}