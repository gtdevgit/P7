package com.example.go4lunch.ui.main.model;

import android.net.Uri;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

public class CurrentUserTest extends TestCase {

    String name;
    String email;
    Uri photoUrl;
    private final String EXPECTED_NAME = "name";
    private final String EXPECTED_EMAIL = "email";
    private final String URL = "https://go4lunch.com";

    CurrentUser currentUser;

    public void setUp() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https://www.go4lunch.com");
        Uri uri = builder.build();
        currentUser = new CurrentUser(EXPECTED_NAME, EXPECTED_EMAIL, uri);
    }

    public void testTestGetName() {
        assertTrue(currentUser.getName().equals(EXPECTED_NAME));
    }

    public void testGetEmail() {
        assertTrue(currentUser.getEmail().equals(EXPECTED_EMAIL));
    }

/*    public void testGetPhotoUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https://www.go4lunch.com");
        Uri expectedUri =  builder.build();
        assertTrue(currentUser.getPhotoUrl().equals(expectedUri));
    }*/
}