package com.example.gtlabgo4lunch.notification;

import java.util.List;

public interface NotificationMessageListener {
    public void onCreatedMessage(String restaurantName, String restaurantAddress, List<String> workmates);
}
