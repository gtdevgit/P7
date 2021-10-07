package com.example.gtlabgo4lunch.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.gtlabgo4lunch.ui.home.listview.ListViewRestaurantFragment;
import com.example.gtlabgo4lunch.ui.home.map.MapFragment;
import com.example.gtlabgo4lunch.ui.home.search.SearchViewInterface;
import com.example.gtlabgo4lunch.ui.home.workmates.WorkmatesFragment;
import com.example.gtlabgo4lunch.ui.main.view.MainActivity;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.tag.Tag;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModel;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    private static final String TAG = Tag.TAG;
    private BottomNavigationView bottomNavigationView;

    private MainViewModel mainViewModel;
    private SearchView searchView;
    private Fragment currentFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // need setHasOptionsMenu to trigger onCreateOptionsMenu
        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // use onCreateOptionsMenu to make sure searchView exits
        acquireSearchView();

        Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "], inflater = [" + inflater + "]");
    }

    private boolean fragmentImplementsSearchViewInterface(Fragment fragment){
        if (fragment != null) {
            Class searchViewInterface = SearchViewInterface.class;
            String searchViewInterfaceName = searchViewInterface.getName();

            Class cf = currentFragment.getClass();
            Class[] intfs = cf.getInterfaces();
            for (Class c : intfs) {
                String name = c.getName();
                if (name.equals(searchViewInterfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void acquireSearchView(){
        searchView = ((MainActivity) getActivity()).getSearchView();
        if (fragmentImplementsSearchViewInterface(currentFragment)) {
            if (currentFragment instanceof MapFragment){
                ((MapFragment) currentFragment).configureSearchView(searchView);
                return;
            }
            if (currentFragment instanceof ListViewRestaurantFragment){
                ((ListViewRestaurantFragment) currentFragment).configureSearchView(searchView);
                return;
            }
        }
    }

    private boolean navigate(@NonNull MenuItem item) {
        MenuItem menuItemSearch = ((MainActivity) getActivity()).getMenuItemSearch();

        switch (item.getItemId()) {
            case R.id.menu_item_map_view:
                loadFragmentMap();
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
        //mapFragment.setSearchView(searchView);
        loadFragment(mapFragment);
    }

    private void loadFragmentListView(){
        ListViewRestaurantFragment listViewRestaurantFragment = new ListViewRestaurantFragment(mainViewModel);
        //listViewRestaurantFragment.setSearchView(searchView);
        loadFragment(listViewRestaurantFragment);
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

        currentFragment = fragment;
        acquireSearchView();
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
        mainViewModel.getErrorMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
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