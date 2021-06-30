package com.example.go4lunch.data.firestore.validation;

import java.util.Calendar;

public class CurrentTimeLimits {
    private long lowLimit;
    private long highLimit;

    public CurrentTimeLimits() {
        Calendar firstHourOffTheDay = Calendar.getInstance();
        // current day at 00h00
        firstHourOffTheDay.set(Calendar.HOUR, 0);
        firstHourOffTheDay.set(Calendar.MINUTE, 0);
        firstHourOffTheDay.set(Calendar.SECOND, 0);
        firstHourOffTheDay.set(Calendar.MILLISECOND, 0);
        lowLimit = firstHourOffTheDay.getTimeInMillis();

        // end of day.
        // in one day there are 24 * 3600 * 1000 ms.
        highLimit = lowLimit + 86400000;
    }

    public boolean isValidDate(long timeMillis){
        return ((timeMillis >= lowLimit) && (timeMillis <= highLimit));
    }

    public long getLowLimit() {
        return lowLimit;
    }

    public long getHighLimit() {
        return highLimit;
    }
}
