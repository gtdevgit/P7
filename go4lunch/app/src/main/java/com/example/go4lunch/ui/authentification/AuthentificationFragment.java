package com.example.go4lunch.ui.authentification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.example.go4lunch.ui.home.HomeViewModel;

public class AuthentificationFragment extends Fragment {

    private AuthentificationViewModel authentificationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        authentificationViewModel =
                new ViewModelProvider(this).get(AuthentificationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_autentification, container, false);
        final TextView textView = root.findViewById(R.id.text_frag_authentification);
        authentificationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
