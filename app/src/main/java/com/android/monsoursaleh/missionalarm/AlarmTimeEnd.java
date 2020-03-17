package com.android.monsoursaleh.missionalarm;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "alarm_time_end")
public class AlarmTimeEnd {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "time")
    private Date alarmEndTime;

    @NonNull
    public Date getAlarmEndTime() {
        return alarmEndTime;
    }

    public void setAlarmEndTime(@NonNull Date alarmEndTime) {
        this.alarmEndTime = alarmEndTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
