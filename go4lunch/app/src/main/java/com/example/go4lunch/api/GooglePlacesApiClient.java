package com.example.go4lunch.api;

import android.util.Log;

import com.example.go4lunch.tag.Tag;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GooglePlacesApiClient {


    public static GooglePlacesApiInterface getClient() {
        Log.d(Tag.TAG, "getClient() called");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GooglePlacesApiInterface api = retrofit.create(GooglePlacesApiInterface.class);
        return api;
    }
}
