package com.example.gtlabgo4lunch;

import android.app.Application;
import android.util.Log;

import com.example.gtlabgo4lunch.notification.NotificationHelper;
import com.example.gtlabgo4lunch.tag.Tag;

public class MainApplication extends Application {

    private static String googleApiKey;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

/*
        // build variable
        String s = com.example.go4lunch.BuildConfig.GOOGLE_PLACES_KEY;
        String S2 = application.getString(R.string.google_place_key);
*/
        googleApiKey = com.example.gtlabgo4lunch.BuildConfig.GOOGLE_PLACES_KEY_P7;
        NotificationHelper.createNotificationChannels(this);
    }

    public static String getGoogleApiKey() {
        return googleApiKey;
    }

    public static Application getApplication() {
        return application;
    }
}
