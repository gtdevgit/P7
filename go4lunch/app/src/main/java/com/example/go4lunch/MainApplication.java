package com.example.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.go4lunch.notification.NotificationHelper;

public class MainApplication extends Application {

    private static String googleApiKey;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiKey = this.getString(R.string.google_api_key);
        application = this;
        NotificationHelper.createNotificationChannels(this);

    }

    //public Application getApplication() {return this;}

    public static String getGoogleApiKey() {
        return googleApiKey;
    }

    public static Application getApplication() {
        return application;
    }
}
