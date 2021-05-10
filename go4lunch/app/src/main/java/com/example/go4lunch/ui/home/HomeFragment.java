package com.example.go4lunch.ui.home;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
    private HomeViewModel homeViewModel;
    private BottomNavigationView bottomNavigationView;

    // GPS
    LocationManager locationManager;
    GPS gps;

    private MainViewModel mainViewModel;

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
        initGPS();
        initViewModel();
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
        Log.d(TAG, "loadFragmentMap() called");
        gps.stopLocalization();
        MapFragment mapFragment = new MapFragment(gps, mainViewModel);
        loadFragment(mapFragment);
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

    public void initGPS(){
        Log.d(TAG, "initGPS() called");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gps = new GPS(locationManager);
    }

    public void initViewModel(){
        mainViewModel = new MainViewModel(new GooglePlacesApiRepository(getString(R.string.google_api_key)));
    }
}