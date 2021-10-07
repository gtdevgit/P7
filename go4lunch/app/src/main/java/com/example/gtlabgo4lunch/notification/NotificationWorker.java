package com.example.gtlabgo4lunch.notification;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.R;
import com.example.gtlabgo4lunch.data.applicationsettings.SettingRepository;
import com.example.gtlabgo4lunch.data.firestore.callback_interface.FailureListener;
import com.example.gtlabgo4lunch.data.googleplace.repository.GooglePlacesApiRepository;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        SettingRepository settingRepository = new SettingRepository();
        if (settingRepository.getAuthorisationNotification()) {
            String uid = FirebaseAuth.getInstance().getUid();
            NotificationHelper.createMessage(uid,
                    new GooglePlacesApiRepository(context.getString(R.string.google_api_key)),
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
            // restart for next day
            NotificationHelper.startNotificationWorker(this.context);
        }
        return Result.success();
    }
}
