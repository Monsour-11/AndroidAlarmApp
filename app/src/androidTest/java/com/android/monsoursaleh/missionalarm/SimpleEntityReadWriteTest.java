package com.android.monsoursaleh.missionalarm;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
/*
    This class tests whether adding an alarm will cause the expected changes in the database.
    Ex: There should be a new row for alarm time and 1 or more rows for alarm day. Also, the
    id of alarm time should correspond with the alarm, while id from alarm day should be
    the same as the alarm time it is assigned to.
 */
@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private AlarmDao mAlarmDao;
    private AlarmDatabase mAlarmDatabase;
    private Alarm mAlarm = new Alarm();
    private AlarmTime mTime = new AlarmTime();
    private List<String> mDay;
    // This is used to be able to observe live data objects.
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        mAlarmDatabase = Room.inMemoryDatabaseBuilder(context, AlarmDatabase.class).build();
        mAlarmDao = mAlarmDatabase.alarmDao();
    }

    @After
    public void closeDb() throws IOException {
        mAlarmDatabase.close();
    }

    @Test
    public void writeAlarmAndRead() throws Exception {
        mAlarm.setName("Wake Up!");
        mAlarm.setSnooze(false);
        mAlarm.setVibrate(true);
        mAlarm.setAlarmSound("Silent");
        mAlarmDao.putAlarm(mAlarm);
        final Observer<Alarm> observer = new Observer<Alarm>() {
            @Override
            public void onChanged(Alarm alarm) {
                // Check if alarm in database has name that was assigned to it.
                MatcherAssert.assertThat(mAlarm.getName(), IsEqual.equalTo(alarm.getName()));
            }
        };

        LiveData<Alarm> byName = mAlarmDao.getAlarm(mAlarm.getName());
        byName.observeForever(observer);
    }

    @Test
    public void writeAlarmTimeAndDays() throws Exception {
        Calendar time = Calendar.getInstance();
        mTime.setHour(time.get(Calendar.HOUR));
        mTime.setMinute(time.get(Calendar.MINUTE));
        mTime.setId(mAlarm.getId());
        mDay.add("Mon");

        // Initialize AlarmDay object and put in database
        AlarmDay day = new AlarmDay();
        day.setId(mAlarm.getId());
        day.setDayName("Mon");
        day.setDay(Calendar.MONDAY);

        // Put AlarmTime and AlarmDay objects in database.
        mAlarmDao.putAlarmTime(mTime);
        mAlarmDao.putAlarmDay(day);

        Observer<AlarmTime> timeObserver = new Observer<AlarmTime>() {
            @Override
            public void onChanged(AlarmTime alarmTime) {
                // Check if time has exact numbers set to it and it coincides with alarm id.
                MatcherAssert.assertThat(mTime.getHour(), IsEqual.equalTo(alarmTime.getHour()));
                MatcherAssert.assertThat(mTime.getMinute(), IsEqual.equalTo(alarmTime.getMinute()));
                MatcherAssert.assertThat(mTime.getId(), IsEqual.equalTo(mAlarm.getId()));
            }
        };

        Observer<List<String>> dayObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                // Check if day has the same name that was assigned in database.
                MatcherAssert.assertThat(strings.get(0), IsEqual.equalTo(mDay.get(0)));
            }
        };

        // Observe live data objects since they receive data asynchronously.
        mAlarmDao.getAlarmTimeInfo(mAlarm.getId()).observeForever(timeObserver);
        mAlarmDao.getDays(mAlarm.getId()).observeForever(dayObserver);
    }

    @Test
    public void writeAlarmTimeEnd() throws Exception {
        // Create new AlarmTimeEnd object instance.
        AlarmTimeEnd alarmEnd = new AlarmTimeEnd();

        // Initialize the date as the current day and time.
        Calendar calendar = Calendar.getInstance();

        // Set the date of the AlarmTimeEnd object.
        alarmEnd.setAlarmEndTime(calendar.getTime());

        // Write this into the database.
        mAlarmDao.putAlarmTimeEnd(alarmEnd);

        // Check if the AlarmTimeEnd object in the database is equivalent to the original.
        MatcherAssert.assertThat(alarmEnd.getAlarmEndTime(),
                IsEqual.equalTo(mAlarmDao.getAlarmTimeEnds().get(0).getAlarmEndTime()));
    }
}
