package com.example.go4lunch.ui.logout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.go4lunch.ui.login.LoginActivity;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.login.LoginViewModel;
import com.example.go4lunch.ui.login.LoginViewModelFactory;
import com.example.go4lunch.ui.main.view.MainActivity;

import org.jetbrains.annotations.NotNull;

import static android.widget.Toast.LENGTH_SHORT;

public class LogoutFragment extends Fragment {

    private static final String TAG = Tag.TAG;

    private LogoutViewModel logoutViewModel;
    private ImageView imageViewUserPicture;
    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private Button buttonLogoutUser;
    private Button buttonDeleteUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // need setHasOptionsMenu to trigger onCreateOptionsMenu
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        configureComponents(root);
        configureViewModel();
        logoutViewModel.loadData();
        return root;
    }

    private void configureComponents(View view){
        textViewUserEmail = view.findViewById(R.id.fragment_logout_textView_user_email);
        textViewUserName = view.findViewById(R.id.fragment_logout_textView_user_name);
        imageViewUserPicture = view.findViewById(R.id.fragment_logout_imageView_user_picture);

        buttonLogoutUser = view.findViewById(R.id.fragment_logout_button_logout);
        buttonLogoutUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "buttonLogoutUser.setOnClickListener.onClick()");
                logoutViewModel.signOutUserFromFirebase(getContext());
            }
        });

        buttonDeleteUser = view.findViewById(R.id.fragment_logout_button_delete_user);
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
    }

    private void configureViewModel(){
        logoutViewModel = new ViewModelProvider(this, LogoutViewModelFactory.getInstance()).get(LogoutViewModel.class);
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
        logoutViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), s, LENGTH_SHORT).show();
            }
        });
        logoutViewModel.getDeletedUserWithSuccessLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean.booleanValue()){
                    Toast.makeText(getContext(), R.string.user_deleted, LENGTH_SHORT).show();
                }
            }
        });
        logoutViewModel.getLogoutViewStateLiveData().observe(getViewLifecycleOwner(), new Observer<LogoutViewState>() {
            @Override
            public void onChanged(LogoutViewState logoutViewState) {
                textViewUserName.setText(logoutViewState.getUserName());
                textViewUserEmail.setText(logoutViewState.getUserEmail());
                if (logoutViewState.getUserPictureUri() !=null && logoutViewState.getUserPictureUri() != Uri.EMPTY) {
                    //load user picture
                    Glide.with(getContext())
                            .load(logoutViewState.getUserPictureUri())
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
                buttonLogoutUser.setEnabled(logoutViewState.getButtonLogoutUserEnabled());
                buttonDeleteUser.setEnabled(logoutViewState.getButtonDeleteUserEnabled());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(Tag.TAG, "SettingFragment.onCreateOptionsMenu() called.");
        MenuItem menuItemSearch = ((MainActivity) getActivity()).getMenuItemSearch();
        menuItemSearch.setVisible(false);
    }
}