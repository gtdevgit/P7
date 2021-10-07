package com.example.gtlabgo4lunch.ui.setting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.gtlabgo4lunch.notification.NotificationHelper;
import com.example.gtlabgo4lunch.tag.Tag;
import com.example.gtlabgo4lunch.ui.main.view.MainActivity;

import org.jetbrains.annotations.NotNull;

public class SettingFragment extends Fragment {

    private TextView textViewPermissionLocalization;
    private CheckBox checkBoxAuthoriseNotification;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private SettingViewModel settingViewModel;

    public SettingFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // need setHasOptionsMenu to trigger onCreateOptionsMenu
        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(Tag.TAG, "SettingFragment.onCreateOptionsMenu() called.");
        MenuItem menuItemSearch = ((MainActivity) getActivity()).getMenuItemSearch();
        menuItemSearch.setVisible(false);
    }
}