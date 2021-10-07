package com.example.gtlabgo4lunch.ui.main.model;

import junit.framework.TestCase;

public class SearchViewResultItemTest extends TestCase {

    private final String EXPECTED_PLACE_ID = "placeId";
    private final String EXPECTED_DESCRIPTION = "description";

    SearchViewResultItem searchViewResultItem;

    public void setUp() throws Exception {
        super.setUp();
        searchViewResultItem = new SearchViewResultItem(EXPECTED_DESCRIPTION, EXPECTED_PLACE_ID);
    }

    public void testGetDescription() {
        assertTrue(searchViewResultItem.getDescription().equals(EXPECTED_DESCRIPTION));
    }

    public void testGetPlaceId() {
        assertTrue(searchViewResultItem.getPlaceId().equals(EXPECTED_PLACE_ID));
    }

    public void testTestToString() {
        assertTrue(searchViewResultItem.toString().equals(EXPECTED_DESCRIPTION));
    }
}