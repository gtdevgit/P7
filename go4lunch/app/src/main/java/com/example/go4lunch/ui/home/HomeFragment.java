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
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        initGPS();
        //load map
        loadFragmentMap();
        return root;
    }

    private boolean navigate(@NonNull MenuItem item) {
        MenuItem menuItemSearch = ((MainActivity) getActivity()).getMenuItemSearch();

        switch (item.getItemId()) {
            case R.id.menu_item_map_view:
                Toast.makeText(this.getContext(), "map view", LENGTH_SHORT).show();
                loadFragmentMap();
                // toto add text edit search
                menuItemSearch.setVisible(true);
                return true;
            case R.id.menu_item_list_view:
                Toast.makeText(this.getContext(), "list view", LENGTH_SHORT).show();
                loadFragmentListView();
                menuItemSearch.setVisible(true);
                return true;
            case R.id.menu_item_workmates:
                Toast.makeText(this.getContext(), "workmates view", LENGTH_SHORT).show();
                loadFragmentWorkmates();
                menuItemSearch.setVisible(false);
                return true;
        }
        return false;
    }

    private void loadFragmentMap(){
        Log.d(TAG, "HomeFragment.loadFragmentMap() called");
        MapFragment mapFragment = new MapFragment(this.mainViewModel);
        loadFragment(mapFragment);
    }

    private void loadFragmentListView(){
        loadFragment(new ListViewRestaurantFragment(mainViewModel));
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

    public void initViewModel(){
        Log.d(TAG, "HomeFragment.initViewModel() called");
        this.mainViewModel = new MainViewModel(
                new GooglePlacesApiRepository(getString(R.string.google_api_key)));
    }

    public void initGPS(){
        Log.d(TAG, "HomeFragment.initGPS() called");
        this.gps = new GPS((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
        this.gps.getLocationMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "HomeFragment.initGPS.onChanged() called with: location = [" + location + "]");
                mainViewModel.setLocation(location);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment.onResume() called");
        gps.startLocalization();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "HomeFragment.onPause() called");
        gps.stopLocalization();
        super.onPause();
    }
}