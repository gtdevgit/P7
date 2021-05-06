package com.example.go4lunch.MyPlace;

import android.content.Context;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.tag.Tag;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MyPlace {

    private static final String TAG = Tag.TAG;
    private static final MyPlace instance = new MyPlace();
    private PlacesClient placeClient;

    private MyPlace() {
    }

    public static final MyPlace getInstance(){
        return instance;
    }

    public void initialize(Context applicationContext, String apiKey){
        Log.d(TAG, "MyPlace.initialize() called with: applicationContext = [" + applicationContext + "], apiKey = [" + apiKey + "]");
        Places.initialize(applicationContext, apiKey);
        placeClient = Places.createClient(applicationContext);
    }

    public PlacesClient getPlaceClient(){
        return  placeClient;
    }

}
