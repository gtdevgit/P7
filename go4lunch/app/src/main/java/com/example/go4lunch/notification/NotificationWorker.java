package com.example.go4lunch.notification;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.api.firestore.ChosenHelper;
import com.example.go4lunch.api.firestore.FailureListener;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListListener;
import com.example.go4lunch.api.firestore.UserRestaurantAssociationListener;
import com.example.go4lunch.models.UserRestaurantAssociation;
import com.example.go4lunch.tag.Tag;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.LogManager;

public class NotificationWorker extends Worker {

    private Context context;

    public NotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Log.d(Tag.TAG, "doWork() called");
        String uid = FirebaseAuth.getInstance().getUid();
        NotificationHelper.createMessage(uid,
                new NotificationMessageListener() {
                    @Override
                    public void onCreatedMessage(String restaurantName, String restaurantAddress, List<String> workmates) {
                        NotificationHelper.sendNotification(context, restaurantName, restaurantAddress, workmates);
                    }
                },
                new FailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                    }
                });

        NotificationHelper.startNotificationWorker(this.context);
        return Result.success();
    }
}