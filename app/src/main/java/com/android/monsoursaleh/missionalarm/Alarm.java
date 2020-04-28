package com.android.monsoursaleh.missionalarm;

import android.net.Uri;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
/*
    This is a class the defines the attributes of each alarm object.
    It also includes the annotations for the room database such as
    entity, primary key, and column info. This is how various alarms
    can be accessed by the user and changed.
 */
@Entity(tableName = "alarm_table")
@TypeConverters({DateConverter.class, UriConverter.class})
public class Alarm {
    private String mName;
    @PrimaryKey(autoGenerate = true)
    private int mId;
    private Uri mAlarmSound;
    private String mRingtone;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    private boolean mVibrate;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    private boolean mSnooze;
    @NonNull
    private Date mTime = Calendar.getInstance().getTime();


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @NonNull
    public Uri getAlarmSound() {
        return mAlarmSound;
    }

    public void setAlarmSound(@NonNull Uri alarmSound) {
        mAlarmSound = alarmSound;
    }

    public boolean isVibrate() {
        return mVibrate;
    }

    public void setVibrate(boolean vibrate) {
        mVibrate = vibrate;
    }

    public boolean isSnooze() {
        return mSnooze;
    }

    public void setSnooze(boolean snooze) {
        mSnooze = snooze;
    }

    @NonNull
    public Date getTime() {
        return mTime;
    }

    public void setTime(@NonNull Date time) {
        mTime = time;
    }

    public String getRingtone() {
        return mRingtone;
    }

    public void setRingtone(String ringtone) {
        mRingtone = ringtone;
    }
}
