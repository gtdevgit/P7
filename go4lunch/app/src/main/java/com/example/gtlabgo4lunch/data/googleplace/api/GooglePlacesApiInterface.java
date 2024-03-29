package com.example.gtlabgo4lunch.data.googleplace.api;

import com.example.gtlabgo4lunch.data.googleplace.model.autocomplete.Autocomplete;
import com.example.gtlabgo4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.gtlabgo4lunch.data.googleplace.model.placesearch.PlaceSearch;

import retrofit2.Call;
import retrofit2.http.GET;
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

    @GET("autocomplete/json?strictbounds")
    Call<Autocomplete>getAutocomplete(
            @Query("input") String input,
            @Query("types") String types,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);

    @GET("textsearch/json")
    Call<PlaceSearch>getTextsearch(
            @Query("query") String query,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);

    @GET("details/json")
    Call<PlaceDetails>getDetails(
            @Query("place_id") String placeId,
            @Query("key") String key);

    /*
    https://developers.google.com/maps/documentation/places/web-service/search#PlaceSearchRequests
     */
    @GET("nearbysearch/json")
    Call<PlaceSearch>getNearbysearch(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);
}
