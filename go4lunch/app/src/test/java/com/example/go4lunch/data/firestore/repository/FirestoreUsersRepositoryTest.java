package com.example.go4lunch.data.firestore.repository;

import junit.framework.TestCase;

public class FirestoreUsersRepositoryTest extends TestCase {

    FirestoreUsersRepository firestoreUsersRepository;
    protected String uid;
    protected String userName;
    protected String userEmail;
    protected String urlPicture;

    public void setUp() {

        firestoreUsersRepository = new FirestoreUsersRepository();
        uid = "UT_uid";
        userName = "UT_userName";
        userEmail = "UT_userEmail";
        urlPicture = "UT_urlPicture";

    }

    public void testCreateUser() {
        setUp();
        firestoreUsersRepository.createUser(uid, userName, userEmail, urlPicture);
    }

    public void testDeleteUser() {
    }

    public void testLoadAllUsers() {
    }
}