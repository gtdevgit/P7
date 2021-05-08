package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.repository.GooglePlacesApiRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private static final String TAG = "go4lunchdebug";

    private GooglePlacesApiRepository googlePlacesApiRepository;
    private final MutableLiveData<Autocomplete> autocompleteData = new MutableLiveData<Autocomplete>();
    private final MutableLiveData<String> error = new MutableLiveData<String>();

    public MainViewModel(GooglePlacesApiRepository googlePlacesApiRepository) {
        Log.d(TAG, "MainViewModel() called with: googlePlacesApiRepository = [" + googlePlacesApiRepository + "]");
        this.googlePlacesApiRepository = googlePlacesApiRepository;
    }

    public MutableLiveData<Autocomplete> getAutocompleteData(){ return autocompleteData; }
    public MutableLiveData<String> getError(){
        return this.error;
    }

    public void loadAutocompleteData(Location location){
        Log.d(TAG, "MainViewModel.loadAutocompleteData() called with: location = [" + location + "]");

        Call<Autocomplete> call = googlePlacesApiRepository.getAutocomplete(location);
        Log.d(TAG, "loadAutocompleteData() 2 " + call);
        call.enqueue(new Callback<Autocomplete>() {
            @Override
            public void onResponse(Call<Autocomplete> call, Response<Autocomplete> response) {
                Log.d(TAG, "MainViewModel.loadAutocompleteData.onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()){
                    Log.d(TAG, "MainViewModel.loadAutocompleteData.onResponse() isSuccessful=true");
                    Log.d(TAG, "response.body = " + response.body());
                    autocompleteData.postValue(response.body());
                } else {
                    Log.d(TAG, "MainViewModel.loadAutocompleteData.onResponse() isSuccessful=false");
                }
            }

            @Override
            public void onFailure(Call<Autocomplete> call, Throwable t) {
                Log.d(TAG, "MainViewModel.loadAutocompleteData.onFailure() " + t.getMessage());
            }
        });
    }
}
