package com.example.go4lunch.ui.setting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.PermissionActivity;
import com.example.go4lunch.R;

public class SettingFragment extends Fragment {

    private TextView textViewPermissionLocalization;
    //private SettingViewModel settingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        textViewPermissionLocalization = root.findViewById(R.id.fragment_setting_textview_permission_localisation);
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            textViewPermissionLocalization.setText(R.string.localisation_allowed);
        } else {
            textViewPermissionLocalization.setText(R.string.localisation_not_allowed);}

        return root;
    }
}