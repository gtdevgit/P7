package com.example.go4lunch.ui.home.listview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.example.go4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.go4lunch.ui.home.search.SearchFragment;
import com.example.go4lunch.ui.main.model.Restaurant;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.detailrestaurant.view.DetailRestaurantActivity;
import com.example.go4lunch.ui.main.viewmodel.MainViewModel;
import com.example.go4lunch.ui.main.viewstate.MainViewState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListViewRestaurantFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private MainViewModel mainViewModel;

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

        configureViewModel();
        configureRecyclerView(root);

        return root;
    }

    private void configureRecyclerView(View view){
        recyclerView = view.findViewById(R.id.fragment_list_view_retaurants_recyclerview);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        listViewRestaurantAdapter = new ListViewRestaurantAdapter(new OnClickListenerRestaurant() {
            @Override
            public void onCLickRestaurant(String placeId) {
                showDetailRestaurant(placeId);
            }
        });
        recyclerView.setAdapter(listViewRestaurantAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void configureSearchView(SearchView searchView){
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ListViewRestaurantAdapter listViewRestaurantAdapter = (ListViewRestaurantAdapter) recyclerView.getAdapter();
                    listViewRestaurantAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
    }

    private void configureViewModel(){
        mainViewModel.getMainViewStateMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<MainViewState>() {
            @Override
            public void onChanged(MainViewState mainViewState) {
                setRestaurants(mainViewState.getRestaurants());
            }
        });
    }

    private void setRestaurants(List<Restaurant> restaurants){
        Log.d(Tag.TAG, "ListViewRestaurantFragment.setRestaurants(restaurants) restaurants.size()=" + restaurants.size());
        progressBar.setVisibility(View.VISIBLE);
        listViewRestaurantAdapter.updateData(restaurants);
        progressBar.setVisibility(View.INVISIBLE);
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
}