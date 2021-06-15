package com.example.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.go4lunch.notification.NotificationHelper;

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationHelper.createNotificationChannels(this);
    }


}
