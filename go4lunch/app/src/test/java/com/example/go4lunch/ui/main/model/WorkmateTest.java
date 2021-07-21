package com.example.go4lunch.ui.main.model;

import junit.framework.TestCase;

public class WorkmateTest extends TestCase {

    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_PICTURE = "picture";
    private final String EXPECTED_PLACE_ID = "placeId";
    private final String EXPECTED_RESTAURANT_NAME = "restaurantName";
    private final String EXPECTED_TEXT_WITHOUT_RESTAURANT = "name";
    private final String EXPECTED_TEXT_WITH_RESTAURANT = "name (restaurantName)";

    Workmate workmate1, workmate2;

    public void setUp() {
        workmate1 = new Workmate(EXPECTED_NAME, EXPECTED_PICTURE, EXPECTED_PLACE_ID, "");
        workmate2 = new Workmate(EXPECTED_NAME, EXPECTED_PICTURE, EXPECTED_PLACE_ID, EXPECTED_RESTAURANT_NAME);
    }

    public void testGetText_without_restaurantName() {
        assertTrue(workmate1.getText().equals(EXPECTED_TEXT_WITHOUT_RESTAURANT));
    }

    public void testGetText_with_restaurantName() {
        assertTrue(workmate2.getText().equals(EXPECTED_TEXT_WITH_RESTAURANT));
    }

    public void testGetUserUrlPicture() {
        assertTrue(workmate1.getUserUrlPicture().equals(EXPECTED_PICTURE));
    }

    public void testGetPlaceId() {
        assertTrue(workmate1.getPlaceId().equals(EXPECTED_PLACE_ID));
    }
}