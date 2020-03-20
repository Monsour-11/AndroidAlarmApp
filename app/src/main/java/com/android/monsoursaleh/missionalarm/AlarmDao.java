package com.android.monsoursaleh.missionalarm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.ForeignKey;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface AlarmDao {

    // Get all alarms from database.
    @Query("SELECT * FROM alarm_table")
    LiveData<List<Alarm>> getAlarms();

    // Selects an alarm from database by name.
    @Query("SELECT * FROM ALARM_TABLE WHERE mName = :name")
    LiveData<Alarm> getAlarm(String name);

    // Get data on alarm end time.
    @Query("SELECT * FROM alarm_time_end")
    List<AlarmTimeEnd> getAlarmTimeEnds();

    // Get all days in string form for alarm.
    @Query("SELECT ALL mDayName FROM days_table WHERE id = :id")
    LiveData<List<String>> getDays(int id);

    // Get all days in string form.
    @Query("SELECT ALL mDayName FROM days_table")
    LiveData<List<String>> getAllDays();

    // Delete a specific day from alarm.
    @Query("DELETE FROM days_table WHERE mAlarmId = :id AND mDayName = :day")
    void deleteDay(int id, String day);

    @Query("SELECT mDayName FROM days_table WHERE mAlarmId = :id")
    List<String> getDaysList(int id);

    // Insert an alarm
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putAlarm(Alarm alarm);

    @Insert
    void putAlarmTimeEnd(AlarmTimeEnd time);

    // Insert an alarm day.
    @Insert
    void putAlarmDay(AlarmDay day);

    // Delete a particular alarm
    @Delete
    void deleteAlarm(Alarm alarm);

}
