package com.android.monsoursaleh.missionalarm;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Alarm.class, AlarmTimeEnd.class, AlarmDay.class},
        version = 4)
@TypeConverters({DateConverter.class})
public abstract class AlarmDatabase extends RoomDatabase {
    private static AlarmDatabase sAlarmDatabase;
    public abstract AlarmDao alarmDao();

    public static AlarmDatabase getDatabase(final Context context) {
        if (sAlarmDatabase == null) {
            synchronized (AlarmDatabase.class) {
                if (sAlarmDatabase == null) {
                    sAlarmDatabase =
                            Room.databaseBuilder(
                            context,
                            AlarmDatabase.class,
                            "alarm_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return sAlarmDatabase;
    }
}
