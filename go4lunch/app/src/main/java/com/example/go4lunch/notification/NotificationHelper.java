package com.example.go4lunch.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.api.firestore.ChosenHelper;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.UserHelper;
import com.example.go4lunch.api.firestore.UserListListener;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.navigation.NavigationActivity;
import com.example.go4lunch.tag.Tag;

import java.util.ArrayList;
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
        Log.d(Tag.TAG, "startNotificationWorker() called with: context = [" + context + "]");
        Calendar currentDate = Calendar.getInstance();

        Calendar dueDate = Calendar.getInstance();
        // Set Execution around hour:
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.MINUTE, 0);

        //dueDate.add(Calendar.SECOND, 10);

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

    public static void stopNotificationWorker(Context context){
        // todo : stopNotificationWorker à faire
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
        final String title = "restaurantName";
        StringBuilder sb = new StringBuilder();
        // sb.append(restaurantName);
        // sb.append(" ");
        // sb.append(restaurantAddress);
        // sb.append(" ");
        for(int i = 0; i < workmatesName.size(); i++){
            sb.append(workmatesName.get(i));
            if (i < workmatesName.size()-1){
                sb.append(", ");
            }
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
            .setContentTitle(restaurantName)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setAutoCancel(true);

        Notification notification = builder.build();

        int notificationId = 1;
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification);
    }

    public static void createMessage(String uid, NotificationMessageListener notificationMessageListener, FailureListener failureListener){
        String restaurant = "restaurant";
        String address = "address";
        List<String> workmatesUid = new ArrayList<>();
        List<String> workmatesName = new ArrayList<>();

        ChosenHelper.getChosenRestaurants(
            new UserRestaurantAssociationListListener() {
                @Override
                public void onGetUserRestaurantAssociationList(List<UserRestaurantAssociation> userRestaurantAssociations) {
                    userRestaurantAssociations = userRestaurantAssociations;
                    // 1 find user chose and check if is valid date
                    String placeId = findUserPlaceId(uid, userRestaurantAssociations);
                    if (!placeId.equals("")) {
                        List<String> workmatesUid = findWorkmatesUid(placeId, userRestaurantAssociations);
                        if (workmatesUid.size() > 0){
                            // find user name
                            UserHelper.getUsersByUidList(workmatesUid,
                                    new UserListListener() {
                                        @Override
                                        public void onGetUsers(List<User> users) {
                                            for (User user : users){
                                                if (!isSameUid(uid, user.getUid())){
                                                    workmatesName.add( user.getUserName());
                                                }
                                                notificationMessageListener.onCreatedMessage(restaurant, address, workmatesName);
                                            }
                                        }
                                    },
                                    new FailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                        }
                        }
                    }
                },
                new FailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    private static boolean isSameUid(String firstUid, String secondUid){
        return firstUid.equals(secondUid);
    }

    private static boolean isSamePlaceId(String firstPlaceId, String secondPlaceId){
        return firstPlaceId.equals(secondPlaceId);
    }

    private static boolean isValidDate(final long firstHourOffTheDay, final long lastHourOffTheDay, final long createdTime){
        return ((createdTime >= firstHourOffTheDay) &&
                (createdTime <= lastHourOffTheDay));
    }

    private static String findUserPlaceId(String uid, List<UserRestaurantAssociation> userRestaurantAssociations){

        Calendar firstHourOffTheDay = Calendar.getInstance();
        // current day at 00h00
        firstHourOffTheDay.set(Calendar.HOUR, 0);
        firstHourOffTheDay.set(Calendar.MINUTE, 0);
        firstHourOffTheDay.set(Calendar.SECOND, 0);
        firstHourOffTheDay.set(Calendar.MILLISECOND, 0);
        // current day at 23h59m59s999
        Calendar lastHourOffTheDay = (Calendar) firstHourOffTheDay.clone();
        lastHourOffTheDay.set(Calendar.HOUR, 23);
        lastHourOffTheDay.set(Calendar.MINUTE, 59);
        lastHourOffTheDay.set(Calendar.SECOND, 59);
        lastHourOffTheDay.set(Calendar.MILLISECOND, 999);

        for (UserRestaurantAssociation userRestaurantAssociation : userRestaurantAssociations) {
            if (isSameUid(uid, userRestaurantAssociation.getUserUid()) &&
                    (isValidDate(firstHourOffTheDay.getTimeInMillis(),
                            lastHourOffTheDay.getTimeInMillis(),
                            userRestaurantAssociation.getCreatedTime()))) {
                return userRestaurantAssociation.getPlaceId();
            }
        }
        return "";
    }

    private static List<String> findWorkmatesUid(String placeId, List<UserRestaurantAssociation> allUserRestaurantAssociations) {
        List<String> workmates = new ArrayList<>();

        Calendar firstHourOffTheDay = Calendar.getInstance();
        // current day at 00h00
        firstHourOffTheDay.set(Calendar.HOUR, 0);
        firstHourOffTheDay.set(Calendar.MINUTE, 0);
        firstHourOffTheDay.set(Calendar.SECOND, 0);
        firstHourOffTheDay.set(Calendar.MILLISECOND, 0);
        // current day at 23h59m59s999
        Calendar lastHourOffTheDay = (Calendar) firstHourOffTheDay.clone();
        lastHourOffTheDay.set(Calendar.HOUR, 23);
        lastHourOffTheDay.set(Calendar.MINUTE, 59);
        lastHourOffTheDay.set(Calendar.SECOND, 59);
        lastHourOffTheDay.set(Calendar.MILLISECOND, 999);

        for (UserRestaurantAssociation userRestaurantAssociation : allUserRestaurantAssociations){
            if (isSamePlaceId(placeId, userRestaurantAssociation.getPlaceId()) &&
                    (isValidDate(firstHourOffTheDay.getTimeInMillis(),
                            lastHourOffTheDay.getTimeInMillis(),
                            userRestaurantAssociation.getCreatedTime()))){
                workmates.add(userRestaurantAssociation.getUserUid());
            }
        }
        return workmates;
    }
}
