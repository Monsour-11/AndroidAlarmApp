package com.android.monsoursaleh.missionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmsHelper {
    public static final int REQUEST_SET_ALARM = 0;

    // TODO: 3/20/20 Create function to find difference between current time and next alarm time.
    // TODO: 3/21/20 Create a function that can find the date of the next alarm given an alarm object.

    /*
    Set an alarm for exact specified time.
     */

    public static void setAlarm(Context context, long timeInMillis) {
        // Access the alarm manager.
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent to broadcast.
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Pending Intent for exact alarm.
        PendingIntent operation = PendingIntent.getBroadcast(context, REQUEST_SET_ALARM,
                intent, 0);

        // Set exact Alarm.
        manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, operation);
    }

    public static Date toDate(Date time) {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    /*
    // Checks whether alarm is first priority or closest to present.
    public static boolean isFirstAlarm(List<AlarmDay> alarmDays, AlarmDay checkAlarm) {
        boolean isFirst = true;
        for (AlarmDay alarm : alarmDays) {
            if (checkAlarm.getDate().after(alarm.getDate())) {
                isFirst = false;
            }
        }
        return isFirst;
    }

    */

    // Remove an alarm from manager.
    public static void cancelAlarm(Context context, PendingIntent operation) {
        // Cancel the following alarm.
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(operation);
    }

    // Find the value of Date of next Monday/Tuesday etc.
    public static Date findNextDay(Date date) {

        // This is the specified date of the alarm.
        Calendar alarmDate = Calendar.getInstance();

        // This is the current date (Today's date).
        Calendar currentDate = Calendar.getInstance();

        // If the days of the week are equal.
        if (currentDate.get(Calendar.DAY_OF_WEEK) == alarmDate.get(Calendar.DAY_OF_WEEK)) {

            // The time of the alarm is in the future.
            if (currentDate.get(Calendar.HOUR_OF_DAY) < alarmDate.get(Calendar.HOUR_OF_DAY)) {
                // Update the currentDate variable to have same time.
                currentDate.setTime(date);
            }
            // The hour values are equal for both dates.
            else if (currentDate.get(Calendar.HOUR_OF_DAY) == alarmDate.get(Calendar.HOUR_OF_DAY)) {
                // The alarm is in the future.
                if (currentDate.get(Calendar.MINUTE) < alarmDate.get(Calendar.MINUTE)) {
                    // Update currentDate to have same date as alarm.
                    currentDate.setTime(date);
                }
            }
        } else {
            // The alarm is on a different day.
            while (currentDate.get(Calendar.DAY_OF_WEEK) != alarmDate.get(Calendar.DAY_OF_WEEK)) {
                currentDate.add(Calendar.DAY_OF_WEEK, 1);
            }

            // Match the time to the given alarm time.
            currentDate.add(Calendar.HOUR_OF_DAY, alarmDate.get(Calendar.HOUR_OF_DAY));
            currentDate.add(Calendar.MINUTE, alarmDate.get(Calendar.MINUTE));
        }

        return currentDate.getTime();
    }
}
