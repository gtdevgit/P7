package com.example.go4lunch.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private BottomNavigationView bottomNavigationView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = root.findViewById(R.id.fragement_home_bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigate(item);
            }
        });

        //load map
        loadFragmentMap();
        return root;
    }

    private boolean navigate(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_map_view:
                Toast.makeText(this.getContext(), "map view", LENGTH_SHORT).show();
                loadFragmentMap();
                return true;
            case R.id.menu_item_list_view:
                Toast.makeText(this.getContext(), "list view", LENGTH_SHORT).show();
                loadFragmentListView();
                return true;
            case R.id.menu_item_workmates:
                Toast.makeText(this.getContext(), "workmates view", LENGTH_SHORT).show();
                loadFragmentWorkmates();
                return true;
        }
        return false;
    }

    private void loadFragmentMap(){
        loadFragment(new MapFragment());
    }

    private void loadFragmentListView(){
        loadFragment(new ListViewFragment());
    }

    private void loadFragmentWorkmates(){
        loadFragment(new WorkmatesFragment());
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_home_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}