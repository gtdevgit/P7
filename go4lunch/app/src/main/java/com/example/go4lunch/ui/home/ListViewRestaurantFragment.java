package com.example.go4lunch.ui.home;

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
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class ListViewRestaurantFragment extends Fragment {

    private static final String TAG = Tag.TAG;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    private MainViewModel mainViewModel;

    private List<Restaurant> restaurantsList;
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

        restaurantsList = new ArrayList<>();
        listViewRestaurantAdapter = new ListViewRestaurantAdapter(restaurantsList);
        recyclerView.setAdapter(listViewRestaurantAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mainViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(TAG, "ListViewRestaurantFragment.onCreateView.getRestaurantsLiveData.onChanged() called with: restaurants = [" + restaurants + "]");
                setRestaurants(restaurants);
            }
        });

        return root;
    }

    public void setRestaurants(List<Restaurant> restaurants){
        Log.d(TAG, "ListViewRestaurantFragment.setRestaurants() called with: restaurants = [" + restaurants + "]");
        progressBar.setVisibility(View.VISIBLE);

        this.restaurantsList.clear();
        this.restaurantsList.addAll(restaurants);
        progressBar.setVisibility(View.INVISIBLE);
        listViewRestaurantAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ListViewRestaurantFragment.onViewCreated() called");
    }
}