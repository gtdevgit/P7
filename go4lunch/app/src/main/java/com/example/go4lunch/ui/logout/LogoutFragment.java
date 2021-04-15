package com.example.go4lunch.ui.logout;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;

public class LogoutFragment extends Fragment {

    private LogoutViewModel logoutViewModel;
    private ImageView imageViewUserPicture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        logoutViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_logout, container, false);

        // user email observer
        final TextView textViewUserEmail = root.findViewById(R.id.fragment_logout__textView_user_email);
        logoutViewModel.getUserEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textViewUserEmail.setText(s);
            }
        });

        // user name observer
        final TextView textViewUserName = root.findViewById(R.id.fragment_logout_textView_user_name);
        logoutViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textViewUserName.setText(s);
            }
        });

        imageViewUserPicture = root.findViewById(R.id.fragment_logout_imageView_user_picture);
        logoutViewModel.getUserPictureUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(@Nullable Uri uri) {
                if (uri == null) {
                    // Clear
                    Glide.with(getContext())
                            .load("")
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewUserPicture);
                } else {
                    Glide.with(getContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewUserPicture);
                }
            }
        });

        logoutViewModel.loadData();
        return root;
    }
}