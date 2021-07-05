package com.example.go4lunch.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.go4lunch.R;
import com.example.go4lunch.data.firestore.callback_interface.FailureListener;
import com.example.go4lunch.data.firestore.callback_interface.UserListListener;
import com.example.go4lunch.data.firestore.callback_interface.UserRestaurantAssociationListListener;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.data.firestore.model.UidPlaceIdAssociation;
import com.example.go4lunch.data.firestore.repository.FirestoreChosenRepository;
import com.example.go4lunch.data.firestore.repository.FirestoreUsersRepository;
import com.example.go4lunch.data.firestore.validation.CurrentTimeLimits;
import com.example.go4lunch.data.googleplace.model.placedetails.PlaceDetails;
import com.example.go4lunch.navigation.NavigationActivity;
import com.example.go4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.example.go4lunch.tag.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHelper {
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "go4lunch default channel";
    private static final int DEFAULT_NOTIFICATION_ID = 1;
    private static final String DEFAULT_WORKREQUEST_TAG = "go4lunch_workrequest_tag";


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

        // for testing
        //dueDate.add(Calendar.SECOND, 30);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        WorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(DEFAULT_WORKREQUEST_TAG)
                .build();

        WorkManager.getInstance(context)
                .enqueue(dailyWorkRequest);
    }

    public static void stopNotificationWorker(Context context){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(DEFAULT_NOTIFICATION_ID);
        WorkManager.getInstance(context).cancelAllWorkByTag(DEFAULT_WORKREQUEST_TAG);
    }

    /**
     * sendNotification
     * @param context
     * @param restaurantName
     * @param restaurantAddress
     * @param workmatesName
     */
    public static void sendNotification(Context context, String restaurantName, String restaurantAddress, List<String> workmatesName) {
        StringBuilder sb = new StringBuilder();

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
            .setContentTitle(restaurantName + " " + restaurantAddress)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setAutoCancel(true);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(DEFAULT_NOTIFICATION_ID, notification);
    }

    public static void createMessage(String uid,
                                     GooglePlacesApiRepository googlePlacesApiRepository,
                                     NotificationMessageListener notificationMessageListener,
                                     FailureListener failureListener){
        List<String> workmatesName = new ArrayList<>();

        FirestoreChosenRepository firestoreChosenRepository = new FirestoreChosenRepository();
        FirestoreUsersRepository firestoreUsersRepository = new FirestoreUsersRepository();
        firestoreChosenRepository.getChosenRestaurants(
            new UserRestaurantAssociationListListener() {
                @Override
                public void onGetUserRestaurantAssociationList(List<UidPlaceIdAssociation> uidPlaceIdAssociations) {
                    uidPlaceIdAssociations = uidPlaceIdAssociations;
                    // 1 find valid user chose (check if is valid date)
                    String placeId = findUserPlaceId(uid, uidPlaceIdAssociations);
                    if (!placeId.equals("")) {
                        // find restaurant's name and address
                        List<String> workmatesUid = findWorkmatesUid(placeId, uidPlaceIdAssociations);
                        if (workmatesUid.size() > 0){
                            // find user name
                            firestoreUsersRepository.getUsersByUidList(workmatesUid,
                                    new UserListListener() {
                                        @Override
                                        public void onGetUsers(List<User> users) {
                                            for (User user : users){
                                                if (!isSameUid(uid, user.getUid())){
                                                    workmatesName.add( user.getUserName());
                                                }
                                                // find restaurantName and restaurantAddress
                                                Call<PlaceDetails> call = googlePlacesApiRepository.getDetails(placeId);
                                                call.enqueue(new Callback<PlaceDetails>() {
                                                    @Override
                                                    public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                                                        Log.d(Tag.TAG, "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                                                        if (response.isSuccessful()) {
                                                            PlaceDetails placeDetails = response.body();
                                                            String restaurantName = placeDetails.getResult().getName();
                                                            String restaurantAddress = placeDetails.getResult().getFormattedAddress();

                                                            notificationMessageListener.onCreatedMessage(restaurantName, restaurantAddress, workmatesName);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<PlaceDetails> call, Throwable t) {
                                                        Log.d(Tag.TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                                                        failureListener.onFailure(new Exception(t));
                                                    }
                                                });
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

    /**
     * Among the list of restaurants chosen by users, search for the current user's choice.
     * Check that the date is valid. The date must be within the current day.
     * @param uid
     * @param uidPlaceIdAssociations
     * @return
     */
    private static String findUserPlaceId(String uid, List<UidPlaceIdAssociation> uidPlaceIdAssociations){
        CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();

        for (UidPlaceIdAssociation uidPlaceIdAssociation : uidPlaceIdAssociations) {
            if (isSameUid(uid, uidPlaceIdAssociation.getUserUid()) &&
                    (currentTimeLimits.isValidDate(uidPlaceIdAssociation.getCreatedTime()))) {
                return uidPlaceIdAssociation.getPlaceId();
            }
        }
        return "";
    }

    private static List<String> findWorkmatesUid(String placeId, List<UidPlaceIdAssociation> allUidPlaceIdAssociations) {
        List<String> workmates = new ArrayList<>();
        CurrentTimeLimits currentTimeLimits = new CurrentTimeLimits();

        for (UidPlaceIdAssociation uidPlaceIdAssociation : allUidPlaceIdAssociations){
            if (isSamePlaceId(placeId, uidPlaceIdAssociation.getPlaceId()) &&
                    (currentTimeLimits.isValidDate(uidPlaceIdAssociation.getCreatedTime()))){
                workmates.add(uidPlaceIdAssociation.getUserUid());
            }
        }
        return workmates;
    }
}
