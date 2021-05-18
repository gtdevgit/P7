package com.example.go4lunch.api.googleplaces;

import com.example.go4lunch.models.Autocomplete;
import com.example.go4lunch.models.googleplaces.Textsearch;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GooglePlacesApiInterface {

    /*
    https://developers.google.com/maps/documentation/places/web-service/overview?hl=fr

    https://maps.googleapis.com/maps/api/place/findplacefromtext/json
        ?input=Museum%20of%20Contemporary%20Art%20Australia
        &inputtype=textquery
        &fields=photos,formatted_address,name,rating,opening_hours,geometry
        &key=[google api key]
     */

    @GET("autocomplete/json")
    Call<JsonObject>getAutocomplete(
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);

    @GET("textsearch/json")
    Call<Textsearch>getTextsearch(
            @Query("query") String query,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);

    @GET("details/json")
    Call<JsonObject>getDetails(
            @Query("place_id") String placeId,
            @Query("key") String key);

    @GET("nearbysearch/json")
    Call<Textsearch>getNearbysearch(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);
}
