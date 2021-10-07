package com.example.gtlabgo4lunch.ui.home.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
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

import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.gtlabgo4lunch.ui.home.search.SearchViewInterface;
import com.example.gtlabgo4lunch.ui.main.model.Restaurant;
import com.example.gtlabgo4lunch.tag.Tag;
import com.example.gtlabgo4lunch.ui.detailrestaurant.view.DetailRestaurantActivity;
import com.example.gtlabgo4lunch.ui.main.model.SearchViewResultItem;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModel;
import com.example.gtlabgo4lunch.ui.main.viewstate.MainViewState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SearchViewInterface {

    private GoogleMap mMap;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private SearchView searchView;

    private Location location;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    MapFragmentAdapter mapFragmentAdapter;

    private MainViewModel mainViewModel;

    public MapFragment(){}

    public MapFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        Log.d(Tag.TAG, "MapFragment()");
        this.mainViewModel = mainViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Tag.TAG, "MapFragment.onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        floatingActionButton = view.findViewById(R.id.fragment_map_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerUserPosition();
            }
        });

        configureRecyclerView(view);
        configureViewModel();

        return view;
    }
    private void configureRecyclerView(View view){
        recyclerView = view.findViewById(R.id.fragment_map_recyclerview);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mapFragmentAdapter = new MapFragmentAdapter(new OnClickListenerRestaurant() {
            @Override
            public void onCLickRestaurant(String placeId) {
                openDetailRestaurantActivity(placeId);
            }
        });

        recyclerView.setAdapter(mapFragmentAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureViewModel(){
        if (mainViewModel != null){
            mainViewModel.getMainViewStateMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<MainViewState>() {
                @Override
                public void onChanged(MainViewState mainViewState) {
                    setLocation(mainViewState.getLocation());
                    setRestaurants(mainViewState.getRestaurants());
                    setSearch(mainViewState.getSearchViewResultVisibility(), mainViewState.getSearchViewResultItems());
                }
            });
            mainViewModel.load();
        }
    };

    @Override
    public void configureSearchView(SearchView searchView) {
        if (searchView != null){
            this.searchView = searchView;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(Tag.TAG, "onQueryTextSubmit() called with: query = [" + query + "]");
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d(Tag.TAG, "onQueryTextChange() called with: newText = [" + newText + "]");
                    mainViewModel.setSearchText(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(Tag.TAG, "MapFragment.onMapReady(). (this.location==null) = " + (this.location==null));
        mMap = googleMap;
        // cycle de vie. Navigation list->map
        if (this.location != null) {
            this.setLocation(this.location);
        }
     }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Tag.TAG, "MapFragment.onViewCreated() called");
        progressBar = view.findViewById(R.id.fragment_map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setLocation(Location location) {
        try{
        Log.d(Tag.TAG, "MapFragment.setLocation(location)");
        progressBar.setVisibility(View.VISIBLE);
        this.location = location;
        if(mMap!=null) {
            progressBar.setVisibility(View.INVISIBLE);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latlng).title("You position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
        progressBar.setVisibility(View.INVISIBLE);
        }
        catch (Exception e) {
            Log.d(Tag.TAG, "setLocation() called with: Exception = [" + e.getMessage() + "]");
        }
    }

    private void openDetailRestaurantActivity(String placeId){
        mainViewModel.clearSearch();
        if (searchView != null && !searchView.isIconified()){
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        Log.d(Tag.TAG, "openDetailRestaurantActivity() called with: placeId = [" + placeId + "]");
        Intent intent = new Intent(getContext(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("placeid", placeId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String placeId = (String) marker.getTag();
        Log.d(Tag.TAG, "onMarkerClick() placeId = [" + placeId + "]");
        openDetailRestaurantActivity(placeId);
        return false;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setRestaurants(List<Restaurant> restaurants){
        Log.d(Tag.TAG, "MapFragment.setRestaurants(restaurants) (mMap==null) = " + (mMap==null));
        if(mMap!=null) {
            progressBar.setVisibility(View.VISIBLE);
            Bitmap bitmapUnselected = drawableToBitmap(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24_primary_color, getContext().getTheme()));
            Bitmap bitmapSelected = drawableToBitmap(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24_selected, getContext().getTheme()));
            for (Restaurant restaurant : restaurants){
                LatLng latlng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap((restaurant.getWorkmatesCount() > 0 ? bitmapSelected : bitmapUnselected)))
                );
                String placeId = restaurant.getPlaceId();
                marker.setTag(placeId);
                mMap.setOnMarkerClickListener(this::onMarkerClick);
            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setSearch(int visibility, List<SearchViewResultItem> searchViewResultItems){
        Log.d(Tag.TAG, "MapFragment.setSearch() called with: visibility = [" + visibility + "], searchViewResultItems = [" + searchViewResultItems + "]");
        recyclerView.setVisibility(visibility);
        mapFragmentAdapter.updateData(searchViewResultItems);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Tag.TAG, "MapFragment.onStart() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "MapFragment.onResume()");
        if ((mMap != null) && (this.location != null) && (mainViewModel != null)) {
            mainViewModel.load();
            mainViewModel.activateChosenRestaurantListener();
        }
    }

    @Override
    public void onPause() {
        Log.d(Tag.TAG, "MapFragment.onPause() called");
        if (mainViewModel != null) {
            this.mainViewModel.removerChosenRestaurantListener();
            this.mainViewModel.clearSearch();
        }
        super.onPause();
    }

    private void centerUserPosition(){
        if ((mMap != null) && (this.location != null)) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
        }
    }
}