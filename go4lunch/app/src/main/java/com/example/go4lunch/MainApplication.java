package com.example.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.go4lunch.notification.NotificationHelper;
import com.facebook.BuildConfig;

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
        googleApiKey = com.example.go4lunch.BuildConfig.GOOGLE_PLACES_KEY;
        NotificationHelper.createNotificationChannels(this);
    }

    public static String getGoogleApiKey() {
        return googleApiKey;
    }

    public static Application getApplication() {
        return application;
    }
}
