package com.android.monsoursaleh.missionalarm;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "alarm_start_times")
public class AlarmTime {
    @ForeignKey(entity = Alarm.class, parentColumns = "id", childColumns = "id",
            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    private int id;
    @PrimaryKey
    @ColumnInfo(name = "alarm_time_start")
    private int mHour;
    private int mMinute;

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
