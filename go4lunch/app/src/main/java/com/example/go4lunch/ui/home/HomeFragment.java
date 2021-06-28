package com.example.go4lunch.ui.home;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.GPS.GPS;
import com.example.go4lunch.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.viewmodel.MainViewModel;
import com.example.go4lunch.viewmodel.MainViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {

    private static final String TAG = Tag.TAG;
    private BottomNavigationView bottomNavigationView;

    private GPS gps;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = root.findViewById(R.id.fragement_home_bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigate(item);
            }
        });

        initViewModel();
        //load map
        loadFragmentMap();
        return root;
    }

    private boolean navigate(@NonNull MenuItem item) {
        MenuItem menuItemSearch = ((MainActivity) getActivity()).getMenuItemSearch();

        switch (item.getItemId()) {
            case R.id.menu_item_map_view:
                loadFragmentMap();
                // toto add text edit search
                menuItemSearch.setVisible(true);
                return true;
            case R.id.menu_item_list_view:
                loadFragmentListView();
                menuItemSearch.setVisible(true);
                return true;
            case R.id.menu_item_workmates:
                loadFragmentWorkmates();
                menuItemSearch.setVisible(false);
                return true;
        }
        return false;
    }

    private void loadFragmentMap(){
        Log.d(TAG, "HomeFragment.loadFragmentMap() called");
        MapFragment mapFragment = new MapFragment(mainViewModel);
        loadFragment(mapFragment);
    }

    private void loadFragmentListView(){
        loadFragment(new ListViewRestaurantFragment(mainViewModel));
    }

    private void loadFragmentWorkmates(){
        loadFragment(new WorkmatesFragment(mainViewModel));
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_home_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void initViewModel(){
        Log.d(TAG, "HomeFragment.initViewModel() called");
        mainViewModel = new ViewModelProvider(getActivity(), MainViewModelFactory.getInstance()).get(MainViewModel.class);
        mainViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showSnackBar(s);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment.onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment.onResume() called");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "HomeFragment.onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "HomeFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "HomeFragment.onDestroy() called");
        super.onDestroy();
    }

    private void showSnackBar(String message){
        ConstraintLayout constraintLayout = ((MainActivity) getActivity()).getConstraintLayout();
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }


}