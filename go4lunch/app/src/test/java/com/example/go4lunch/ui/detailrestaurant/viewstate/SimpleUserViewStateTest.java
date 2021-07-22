package com.example.go4lunch.ui.detailrestaurant.viewstate;

import junit.framework.TestCase;

public class SimpleUserViewStateTest extends TestCase {

    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_URL_PICTURE = "urlPicture";
    SimpleUserViewState simpleUserViewState;

    public void setUp() throws Exception {
        super.setUp();
        simpleUserViewState = new SimpleUserViewState(EXPECTED_NAME, EXPECTED_URL_PICTURE);
    }

    public void testTestGetName() {
        assertEquals(simpleUserViewState.getName(), EXPECTED_NAME);
    }

    public void testGetUrlPicture() {
        assertEquals(simpleUserViewState.getUrlPicture(), EXPECTED_URL_PICTURE);
    }
}