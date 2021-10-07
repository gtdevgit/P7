package com.example.go4lunch.ui.detailrestaurant.viewstate;

import com.example.gtlabgo4lunch.ui.detailrestaurant.viewstate.DetailRestaurantViewState;
import com.example.gtlabgo4lunch.ui.detailrestaurant.viewstate.SimpleUserViewState;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class DetailRestaurantViewStateTest extends TestCase {

    private final String EXPECTED_PLACE_ID = "placeId";
    private final String EXPECTED_URL_PICTURE = "urlPicture";
    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_INFO = "info";
    private final boolean EXPECTED_IS_CHOSEN_BY_CURRENT_USER = true;
    private final int EXPECTED_STAR_1_COLOR = 1;
    private final int EXPECTED_STAR_2_COLOR = 2;
    private final int EXPECTED_STAR_3_COLOR = 3;
    private final String EXPECTED_PHONE_NUMBER = "phoneNumber";
    private final boolean EXPECTED_IS_LIKED_BY_CURRENT_USER = true;
    private final String EXPECTED_WEB_SITE = "webSite";
    private final List<SimpleUserViewState> EXPECTED_WORKMATES = new ArrayList<>();

    DetailRestaurantViewState detailRestaurantViewState;

    public void setUp() throws Exception {
        super.setUp();
        EXPECTED_WORKMATES.add(new SimpleUserViewState(EXPECTED_NAME, EXPECTED_URL_PICTURE));
        detailRestaurantViewState = new DetailRestaurantViewState(EXPECTED_PLACE_ID,
                EXPECTED_URL_PICTURE,
                EXPECTED_NAME,
                EXPECTED_INFO,
                EXPECTED_IS_CHOSEN_BY_CURRENT_USER,
                EXPECTED_STAR_1_COLOR,
                EXPECTED_STAR_2_COLOR,
                EXPECTED_STAR_3_COLOR,
                EXPECTED_PHONE_NUMBER,
                EXPECTED_IS_LIKED_BY_CURRENT_USER,
                EXPECTED_WEB_SITE,
                EXPECTED_WORKMATES);
    }

    public void testGetPlaceId() {
        assertEquals(detailRestaurantViewState.getPlaceId(), EXPECTED_PLACE_ID);
    }

    public void testGetUrlPicture() {
        assertEquals(detailRestaurantViewState.getUrlPicture(), EXPECTED_URL_PICTURE);
    }

    public void testTestGetName() {
        assertEquals(detailRestaurantViewState.getName(), EXPECTED_NAME);
    }

    public void testGetInfo() {
        assertEquals(detailRestaurantViewState.getInfo(), EXPECTED_INFO);
    }

    public void testIsChosenByCurrentUser() {
        assertEquals(detailRestaurantViewState.isChosenByCurrentUser(), EXPECTED_IS_CHOSEN_BY_CURRENT_USER);
    }

    public void testGetStar1Color() {
        assertEquals(detailRestaurantViewState.getStar1Color(), EXPECTED_STAR_1_COLOR);
    }

    public void testGetStar2Color() {
        assertEquals(detailRestaurantViewState.getStar2Color(), EXPECTED_STAR_2_COLOR);
    }

    public void testGetStar3Color() {
        assertEquals(detailRestaurantViewState.getStar3Color(), EXPECTED_STAR_3_COLOR);
    }

    public void testGetPhoneNumber() {
        assertEquals(detailRestaurantViewState.getPhoneNumber(), EXPECTED_PHONE_NUMBER);
    }

    public void testIsLikedByCurrentUser() {
        assertEquals(detailRestaurantViewState.isLikedByCurrentUser(), EXPECTED_IS_LIKED_BY_CURRENT_USER);
    }

    public void testGetWebsite() {
        assertEquals(detailRestaurantViewState.getWebsite(), EXPECTED_WEB_SITE);
    }

    public void testGetWorkmates() {
        assertEquals(detailRestaurantViewState.getWorkmates(), EXPECTED_WORKMATES);
    }
}