package com.android.monsoursaleh.missionalarm;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

public class AlarmRepository {
    private static AlarmRepository sRepository;
    private AlarmDatabase mDatabase;
    private Context applicationContext;

    private AlarmRepository(Context context) {
        synchronized (AlarmRepository.class) {
            if (sRepository == null) {
                mDatabase = AlarmDatabase.getDatabase(context);
                applicationContext = context.getApplicationContext();
            }
        }
    }

    public static AlarmRepository getRepository(Context context) {
        if (sRepository == null) {
            sRepository = new AlarmRepository(context);
        }
        return sRepository;
    }

    public LiveData<List<Alarm>> getAlarms() {
        return mDatabase.alarmDao().getAlarms();
    }

    public void addAlarm(Alarm alarm) {
        // Place alarm in database and set exact alarm using alarm manager.
        mDatabase.alarmDao().putAlarm(alarm);
    }


    public LiveData<List<String>> getDays(int id) {
        return mDatabase.alarmDao().getDays(id);
    }

    public void deleteAlarm(Alarm alarm) {
        // Delete alarm from database and delete all alarmDays.
        mDatabase.alarmDao().deleteAlarm(alarm);
    }

    public LiveData<List<String>> getAllDays() {
        return mDatabase.alarmDao().getAllDays();
    }


    public LiveData<Alarm> getAlarm(String name) {
        return mDatabase.alarmDao().getAlarm(name);
    }


    public void putAlarmDay(AlarmDay day) {
        mDatabase.alarmDao().putAlarmDay(day);
    }


    public void deleteDay(int id, String day) {
        mDatabase.alarmDao().deleteDay(id, day);
    }

}
