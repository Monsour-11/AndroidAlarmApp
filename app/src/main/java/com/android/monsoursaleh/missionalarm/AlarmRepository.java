package com.android.monsoursaleh.missionalarm;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    // Get all alarm times.
    public LiveData<List<AlarmTime>> getAlarmTimes() {
        return mDatabase.alarmDao().getAlarmTimes();
    }

    public LiveData<AlarmTime> getAlarmTime(int id) {
        return mDatabase.alarmDao().getAlarmTimeInfo(id);
    }

    public LiveData<List<AlarmDay>> getAlarmDays() {
        return mDatabase.alarmDao().getAlarmDays();
    }

    public LiveData<List<String>> getDays(int id) {
        return mDatabase.alarmDao().getDays(id);
    }

    public void deleteAlarm(Alarm alarm) {
        // Delete alarm from database and delete all alarmDays.
        mDatabase.alarmDao().deleteAlarm(alarm);
    }

    public void changeName(String name, int id) {
        mDatabase.alarmDao().changeName(name, id);
    }

    public void changeSound(String sound, int id) {
        mDatabase.alarmDao().changeSound(sound, id);
    }

    public void changeSnooze(boolean isSnooze, int id) {
        mDatabase.alarmDao().changeSnooze(isSnooze, id);
    }

    public void changeVibrate(boolean isVibrate, int id) {
        mDatabase.alarmDao().changeVibrate(isVibrate, id);
    }


    public LiveData<Alarm> getAlarm(String name) {
        return mDatabase.alarmDao().getAlarm(name);
    }

    public void putAlarmTime(AlarmTime time) {
        mDatabase.alarmDao().putAlarmTime(time);
    }

    public void putAlarmDay(AlarmDay day) {
        mDatabase.alarmDao().putAlarmDay(day);
    }

    public void deleteAlarmDay(AlarmDay day) {
        mDatabase.alarmDao().deleteAlarmDay(day);
    }

}
