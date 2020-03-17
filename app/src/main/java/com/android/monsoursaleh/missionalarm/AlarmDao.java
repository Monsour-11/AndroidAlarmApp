package com.android.monsoursaleh.missionalarm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface AlarmDao {
    // Selects an alarm from database by name.
    @Query("SELECT * FROM ALARM_TABLE WHERE mName = :name")
    LiveData<Alarm> getAlarm(String name);


    // Selects all alarms from database.
    @Query("SELECT * FROM ALARM_TABLE")
    LiveData<List<Alarm>> getAlarms();

    @Query("UPDATE alarm_table SET mName = :name WHERE mid = :id")
    void changeName(String name, int id);

    @Query("UPDATE alarm_table SET mAlarmSound = :sound WHERE mid = :id")
    void changeSound(String sound, int id);

    @Query("UPDATE alarm_table SET mSnooze = :Snooze WHERE mid = :id")
    void changeSnooze(boolean Snooze, int id);

    @Query("UPDATE alarm_table SET mVibrate = :Vibrate WHERE mid = :id")
    void changeVibrate(boolean Vibrate, int id);

    @Query("SELECT mAlarmSound from ALARM_TABLE WHERE mName = :name")
    String getAlarmSound(String name);

    // Get alarm time information from database
    @Query("SELECT * FROM alarm_start_times WHERE id = :id")
    LiveData<AlarmTime> getAlarmTimeInfo(int id);

    // Get all alarm times.
    @Query("SELECT * FROM alarm_start_times")
    LiveData<List<AlarmTime>> getAlarmTimes();

    // Get all alarm days.
    @Query("SELECT * FROM days_table")
    LiveData<List<AlarmDay>> getAlarmDays();

    // Get data on alarm end time.
    @Query("SELECT * FROM alarm_time_end")
    List<AlarmTimeEnd> getAlarmTimeEnds();

    // Get all days in string from for alarm.
    @Query("SELECT ALL mDayName FROM days_table WHERE id = :id")
    LiveData<List<String>> getDays(int id);

    // Insert an alarm
    @Insert
    void putAlarm(Alarm alarm);

    @Insert
    void putAlarmTimeEnd(AlarmTimeEnd time);

    @Insert
    void putAlarmTime(AlarmTime time);

    @Insert
    void putAlarmDay(AlarmDay day);

    // Delete a particular alarm
    @Delete
    void deleteAlarm(Alarm alarm);

    // This remove a particular alarm day from database.
    @Delete
    void deleteAlarmDay(AlarmDay day);


}
