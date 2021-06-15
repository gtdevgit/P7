package com.example.go4lunch.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.navigation.NavigationActivity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationHelper {
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "go4lunch default channel";

    /**
     * createNotificationChannels
     * @param context
     */
    public static void createNotificationChannels(Context context)  {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL,
                    "go4lunch notification",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This go4lunch channel notification");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void startNotificationWorker(Context context){
        Calendar currentDate = Calendar.getInstance();

        int h = currentDate.get(Calendar.HOUR_OF_DAY);
        if (h < 24){
            h = h + 1;
        } else
            h = 0;

        Calendar dueDate = Calendar.getInstance();
        // Set Execution around hour:
        dueDate.set(Calendar.HOUR_OF_DAY, h);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        WorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context)
                .enqueue(dailyWorkRequest);
    }

    /**
     * sendNotification
     * @param context
     * @param restaurantName
     * @param restaurantAddress
     * @param workmatesName
     */
    public static void sendNotification(Context context, String restaurantName, String restaurantAddress, List<String> workmatesName) {

        // nom du restaurant
        // adresse
        // liste des collégues
        final String title = "It's time for lunch 2";
        StringBuilder sb = new StringBuilder();
        sb.append(restaurantName);
        sb.append(" ");
        sb.append(restaurantAddress);
        sb.append(" ");
        for (String name : workmatesName) {
            sb.append(name);
            sb.append(" ");
        }
        String message = sb.toString();

        // to lunch applicaiton from notification
        Intent notificationIntent = new Intent(context, NavigationActivity.class);
        // Set the Activity to start in a new, empty task
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setAutoCancel(true);

        Notification notification = builder.build();

        int notificationId = 1;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification);
    }
}
