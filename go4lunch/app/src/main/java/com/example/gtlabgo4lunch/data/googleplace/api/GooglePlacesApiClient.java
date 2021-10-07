package com.example.gtlabgo4lunch.data.googleplace.api;

import android.util.Log;

import com.example.gtlabgo4lunch.tag.Tag;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GooglePlacesApiClient {

    public static GooglePlacesApiInterface getClient() {
        Log.d(Tag.TAG, "GooglePlacesApiClient.getClient() called");

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // use this to set the log detail level
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        okHttpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();

        GooglePlacesApiInterface api = retrofit.create(GooglePlacesApiInterface.class);
        return api;
    }
}
