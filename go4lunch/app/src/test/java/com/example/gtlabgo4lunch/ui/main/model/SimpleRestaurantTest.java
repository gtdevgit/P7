package com.example.gtlabgo4lunch.ui.main.model;

import junit.framework.TestCase;

public class SimpleRestaurantTest extends TestCase {

    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_PLACE_ID = "placeId";

    SimpleRestaurant simpleRestaurant;

    public void setUp() throws Exception {
        super.setUp();
        simpleRestaurant = new SimpleRestaurant(EXPECTED_PLACE_ID, EXPECTED_NAME);
    }

    public void testGetPlaceId() {
        assertTrue(simpleRestaurant.getPlaceId().equals(EXPECTED_PLACE_ID));
    }

    public void testTestGetName() {
        assertTrue(simpleRestaurant.getName().equals(EXPECTED_NAME));
    }
}