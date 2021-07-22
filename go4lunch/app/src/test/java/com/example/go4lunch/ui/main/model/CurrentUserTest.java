package com.example.go4lunch.ui.main.model;

import android.net.Uri;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

public class CurrentUserTest extends TestCase {

    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_EMAIL = "email";

    CurrentUser currentUser;

    public void setUp() {
        currentUser = new CurrentUser(EXPECTED_NAME, EXPECTED_EMAIL, null);
    }

    public void testTestGetName() {
        assertTrue(currentUser.getName().equals(EXPECTED_NAME));
    }

    public void testGetEmail() {
        assertTrue(currentUser.getEmail().equals(EXPECTED_EMAIL));
    }

}