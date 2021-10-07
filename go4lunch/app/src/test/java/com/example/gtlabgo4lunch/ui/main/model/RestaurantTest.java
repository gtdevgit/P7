package com.example.go4lunch.ui.main.model;

import com.example.gtlabgo4lunch.ui.main.model.Restaurant;

import junit.framework.TestCase;

public class RestaurantTest extends TestCase {
    private final String EXPECTED_PLACE_ID = "placeId";
    private final String EXPECTED_NAME = "name";
    private final double EXPECTED_LATITUDE = 123.456;
    private final double EXPECTED_LONGITUDE = 789.123;
    private final float EXPECTED_DISTANCE = 456.789f;
    private final String EXPECTED_INFO = "info";
    private final int EXPECTED_OPEN_NOW_RESOURCE_STRING = 2;
    private final int EXPECTED_WORKMATES_COUNT = 1;
    private final double EXPECTED_RATING = 101.202;
    private final String EXPECTED_URL_PICTURE = "urlPicture";
    private final int EXPECTED_COUNT_LIKE = 3;
    private final String EXPECTED_FORMATED_DISTANCE = "457 m";

    Restaurant restaurant;

    public void setUp() throws Exception {
        super.setUp();
        restaurant = new Restaurant(EXPECTED_PLACE_ID,
                EXPECTED_NAME,
                EXPECTED_LATITUDE,
                EXPECTED_LONGITUDE,
                EXPECTED_DISTANCE,
                EXPECTED_INFO,
                EXPECTED_OPEN_NOW_RESOURCE_STRING,
                EXPECTED_WORKMATES_COUNT,
                EXPECTED_RATING,
                EXPECTED_URL_PICTURE,
                EXPECTED_COUNT_LIKE);
    }

    public void testGetPlaceId() {
        assertEquals(restaurant.getPlaceId(), EXPECTED_PLACE_ID);
    }

    public void testTestGetName() {
        assertEquals(restaurant.getName(), EXPECTED_NAME);
    }

    public void testGetLatitude() {
        assertEquals(restaurant.getLatitude(), EXPECTED_LATITUDE);
    }

    public void testGetLongitude() {
        assertEquals(restaurant.getLongitude(), EXPECTED_LONGITUDE);
    }

    public void testGetDistance() {
        assertEquals(restaurant.getDistance(), EXPECTED_DISTANCE);
    }

    public void testGetInfo() {
        assertEquals(restaurant.getInfo(), EXPECTED_INFO);
    }

    public void testGetOpenNowResourceString() {
        assertEquals(restaurant.getOpenNowResourceString(), EXPECTED_OPEN_NOW_RESOURCE_STRING);
    }

    public void testGetWorkmatesCount() {
        assertEquals(restaurant.getWorkmatesCount(), EXPECTED_WORKMATES_COUNT);
    }

    public void testGetRating() {
        assertEquals(restaurant.getRating(), EXPECTED_RATING);
    }

    public void testGetUrlPicture() {
        assertEquals(restaurant.getUrlPicture(), EXPECTED_URL_PICTURE);
    }

    public void testGetCountLike() {
        assertEquals(restaurant.getCountLike(), EXPECTED_COUNT_LIKE);
    }

    public void testGetFormatedDistance() {
        assertEquals(restaurant.getFormatedDistance(), EXPECTED_FORMATED_DISTANCE);
    }
}