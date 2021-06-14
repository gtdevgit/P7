package com.example.go4lunch.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.navigation.NavigationActivity;

public class NotificationHelper {
    public static void sendNotification(Context context, String message) {

        // nom du restaurant
        // adresse
        // liste des collégues
        final String title = "It's time for lunch 2";

        // to lunch applicaiton from notification
        Intent notificationIntent = new Intent(context, NavigationActivity.class);
        // Set the Activity to start in a new, empty task
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainApplication.DEFAULT_NOTIFICATION_CHANNEL);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setCategory(NotificationCompat.CATEGORY_SOCIAL);

        Notification notification = builder.build();

        int notificationId = 1;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification);
    }
}
