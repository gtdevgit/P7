package com.example.go4lunch.ui.logout;

import android.net.Uri;

import junit.framework.TestCase;

public class LogoutViewStateTest extends TestCase {

    private final String EXPECTED_USER_EMAIL = "userEmail";
    private final String EXPECTED_USER_NAME = "userName";
    private final boolean EXPECTED_BUTTON_DELETE_USER_ENABLED = true;
    private final boolean EXPECTED_BUTTON_LOGOUT_USER_ENABLED = true;

    LogoutViewState logoutViewState;

    public void setUp() throws Exception {
        super.setUp();
        logoutViewState = new LogoutViewState(EXPECTED_USER_EMAIL,
                EXPECTED_USER_NAME,
                null,
                EXPECTED_BUTTON_DELETE_USER_ENABLED,
                EXPECTED_BUTTON_LOGOUT_USER_ENABLED);
    }

    public void testGetUserEmail() {
        assertEquals(logoutViewState.getUserEmail(), EXPECTED_USER_EMAIL);
    }

    public void testGetUserName() {
        assertEquals(logoutViewState.getUserName(), EXPECTED_USER_NAME);
    }

    public void testGetButtonDeleteUserEnabled() {
        boolean deleteEnabled = logoutViewState.getButtonDeleteUserEnabled();
        assertEquals(deleteEnabled, EXPECTED_BUTTON_DELETE_USER_ENABLED);
    }

    public void testGetButtonLogoutUserEnabled() {
        boolean logoutEnabled = logoutViewState.getButtonLogoutUserEnabled();
        assertEquals(logoutEnabled, EXPECTED_BUTTON_LOGOUT_USER_ENABLED);
    }

}