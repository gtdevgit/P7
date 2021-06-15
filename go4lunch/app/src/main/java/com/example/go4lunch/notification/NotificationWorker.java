package com.example.go4lunch.notification;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        List<String> workmates = new ArrayList<>();
        workmates.add("Jean");
        workmates.add("Alain");
        workmates.add("Kevin");
        workmates.add("Lucie");

        NotificationHelper.sendNotification(this.context, "LE ZING", "10 rue du bois", workmates);

        NotificationHelper.startNotificationWorker(this.context);

        return Result.success();
    }
}
