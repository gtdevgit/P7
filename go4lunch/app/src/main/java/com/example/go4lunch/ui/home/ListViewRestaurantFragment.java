package com.example.go4lunch.ui.home;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.go4lunch.R;
import com.example.go4lunch.models.viewstate.Restaurant;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.detailrestaurant.DetailRestaurantActivity;
import com.example.go4lunch.viewmodel.MainViewModel;

import java.util.List;

public class ListViewRestaurantFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    private MainViewModel mainViewModel;

    private Location location;
    ListViewRestaurantAdapter listViewRestaurantAdapter;

    public ListViewRestaurantFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        this.mainViewModel = mainViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list_view_restaurants, container, false);
        progressBar = root.findViewById(R.id.fragment_list_view_retaurants_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = root.findViewById(R.id.fragment_list_view_retaurants_recyclerview);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        listViewRestaurantAdapter = new ListViewRestaurantAdapter(new OnClickListenerRestaurant() {
            @Override
            public void onCLickRestaurant(Restaurant restaurant) {
                showDetailRestaurant(restaurant.getPlaceId());
            }
        });
        recyclerView.setAdapter(listViewRestaurantAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        return root;
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    private void setRestaurants(List<Restaurant> restaurants){
        Log.d(Tag.TAG, "ListViewRestaurantFragment.setRestaurants(restaurants) restaurants.size()=" + restaurants.size());
        progressBar.setVisibility(View.VISIBLE);
        listViewRestaurantAdapter.updateData(restaurants);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onViewCreated() called");
    }

    private void showDetailRestaurant(String placeId){
        Intent intent;
        intent = new Intent(this.getActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("placeid", placeId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onStart() called");
        mainViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(Tag.TAG, "ListViewRestaurantFragment: getLocationLiveData.onChanged(location)");
                setLocation(location);
            }
        });

        mainViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(Tag.TAG, "ListViewRestaurantFragment: getRestaurantsLiveData.onChanged(restaurants)");
                setRestaurants(restaurants);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onResume() called");
        this.mainViewModel.activateChosenRestaurantListener();
        this.mainViewModel.activateLikedRestaurantListener();
    }

    @Override
    public void onPause() {
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onPause() called");
        this.mainViewModel.removerChosenRestaurantListener();
        this.mainViewModel.removeLikedRestaurantListener();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onDestroy() called");
        super.onDestroy();
    }
}