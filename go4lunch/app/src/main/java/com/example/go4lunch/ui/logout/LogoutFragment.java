package com.example.go4lunch.ui.logout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.firebase.Authentication;
import com.example.go4lunch.firebase.LoginActivity;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.SettingActivity;

import static android.widget.Toast.LENGTH_SHORT;

public class LogoutFragment extends Fragment {

    private static final String TAG = Tag.TAG;

    private LogoutViewModel logoutViewModel;
    private ImageView imageViewUserPicture;
    private Button buttonLogoutUser;
    private Button buttonDeleteUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        logoutViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
        //callback after logout user
        logoutViewModel.setListenerLogoutUser(new ListenerLogoutUser() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "logoutViewModel.onSuccess()");
                // => go to login activity
                if (!Authentication.isConnected()) {
                    Intent intent;
                    intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
            @Override
            public void onFailureDelete(String message) {
                Log.d(TAG, "logoutViewModel.onFailureDelete() " + message);
                Toast.makeText(getContext(), getString(R.string.error_deleting_account), LENGTH_SHORT).show();
            }
            @Override
            public void onFailureLogout(String message){
                Log.d(TAG, "logoutViewModel.onFailureLogout() " + message);
                Toast.makeText(getContext(), getString(R.string.error_disconnecting_account), LENGTH_SHORT).show();
            }
        });

            // user email observer
        final TextView textViewUserEmail = root.findViewById(R.id.fragment_logout__textView_user_email);

        logoutViewModel.getUserEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "textViewUserEmail.setText() " + s);
                if (s != null && !s.isEmpty()) {
                    textViewUserEmail.setText(s);
                } else {
                    textViewUserEmail.setText(R.string.no_user_email);
                }
            }
        });

        // user name observer
        final TextView textViewUserName = root.findViewById(R.id.fragment_logout_textView_user_name);
        logoutViewModel.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "textViewUserName.setText() " + s);
                if (s != null && !s.isEmpty()) {
                    textViewUserName.setText(s);
                } else {
                    textViewUserName.setText(R.string.no_user_name_found);
                }
            }
        });

        imageViewUserPicture = root.findViewById(R.id.fragment_logout_imageView_user_picture);
        logoutViewModel.getUserPictureUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(@Nullable Uri uri) {
                Log.d(TAG, "imageViewUserPicture->onChanged() called with: uri = [" + uri + "]");
                if (uri !=null && uri != Uri.EMPTY) {
                    //load user picture
                    Glide.with(getContext())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewUserPicture);
                } else {
                    // Clear picture
                    Glide.with(getContext())
                            .load("")
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageViewUserPicture);
                }
            }
        });

        buttonLogoutUser = root.findViewById(R.id.fragment_logout_button_logout);
        buttonLogoutUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonLogoutUser.setOnClickListener.onClick()");
                logoutViewModel.signOutUserFromFirebase(getContext());
            }
        });

        buttonDeleteUser = root.findViewById(R.id.fragment_logout_button_delete_user);
        buttonDeleteUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonDeleteUser.setOnClickListener.onClick()");
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.confirm_delete_account)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logoutViewModel.deleteUserFromFirebase(getContext());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        logoutViewModel.loadData();
        return root;
    }
}