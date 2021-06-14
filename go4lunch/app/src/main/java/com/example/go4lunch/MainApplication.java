package com.example.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MainApplication extends Application {
    public static final  String DEFAULT_NOTIFICATION_CHANNEL = "go4lunch default channel";

    @Override
    public void onCreate() {
        super.onCreate();

        this.createNotificationChannels();
    }

    private void createNotificationChannels()  {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL,
                    "go4lunch notification",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This go4lunch channel notification");

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
