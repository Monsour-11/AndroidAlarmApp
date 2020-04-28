package com.android.monsoursaleh.missionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Calendar;

// This executes some code when the alarm starts.
public class AlarmReceiver extends BroadcastReceiver {
    private static final int REQUEST_SET_ALARM = 0;
    private static final String EXTRA_RINGTONE_URI = "ringtone_uri";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Open up activity for alarm and give the ringtone to the class handling the alarm.
        Intent newIntent = new Intent(context, AlarmScreenActivity.class);
        newIntent.putExtra(EXTRA_RINGTONE_URI, intent.getParcelableExtra(EXTRA_RINGTONE_URI));
        context.startActivity(newIntent);
    }

    public static void setAlarm(Context context, Uri ringtone) {
        // Calendar object with current time.
        Calendar time = Calendar.getInstance();

        // Add 30 seconds to the calendar object.
        time.add(Calendar.SECOND, 60);

        // Intent to start alarm receiver class.
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Place the ringtone for the alarm as an extra in the intent.
        intent.putExtra(EXTRA_RINGTONE_URI, ringtone);

        // Pending intent for alarm.
        PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_SET_ALARM, intent, 0);

        // Get the alarm manager service.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set alarm for exact time.
        am.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), sender);
    }
}
