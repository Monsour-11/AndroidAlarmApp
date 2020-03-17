package com.android.monsoursaleh.missionalarm;

import android.database.Cursor;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "days_table")
public class AlarmDay {
    @PrimaryKey
    @ForeignKey(entity = AlarmTime.class, parentColumns = "id", childColumns = "id",
            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    private int id;
    private int mDay;
    private String mDayName;

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public String getDayName() {
        return mDayName;
    }

    public void setDayName(String dayName) {
        mDayName = dayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
