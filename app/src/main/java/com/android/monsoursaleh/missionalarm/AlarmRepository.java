package com.android.monsoursaleh.missionalarm;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmRepository {
    private static String TAG_REPO = "alarm_repo";
    private LiveData<List<Alarm>> mAlarms;
    private LiveData<List<String>> mDays;
    private static AlarmRepository sRepository;
    private AlarmDatabase mDatabase;
    private Context applicationContext;

    private AlarmRepository(Context context) {
        synchronized (AlarmRepository.class) {
            if (sRepository == null) {
                mDatabase = AlarmDatabase.getDatabase(context);
                applicationContext = context.getApplicationContext();
                mAlarms = mDatabase.alarmDao().getAlarms();
                mDays = mDatabase.alarmDao().getAllDays();
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
        return mAlarms;
    }

    public void addAlarm(Alarm alarm) {
        // Place alarm in database and set exact alarm using alarm manager.
        new PutAlarmAsyncTask(mDatabase.alarmDao()).execute(alarm);
    }


    public LiveData<List<String>> getDays(int id) {
        return mDatabase.alarmDao().getDays(id);
    }

    public void deleteAlarm(Alarm alarm) {
        // Delete alarm from database and delete all alarmDays.
        new DeleteAlarmAsyncTask(mDatabase.alarmDao()).execute(alarm);
    }

    public LiveData<List<String>> getAllDays() {
        return mDays;
    }


    public LiveData<Alarm> getAlarm(String name) {
        return mDatabase.alarmDao().getAlarm(name);
    }

    public void putAlarmDay(AlarmDay day) {
        new PutAlarmDayAsyncTask(mDatabase.alarmDao()).execute(day);
    }


    public void deleteDay(int id, String day) {
        new DeleteAlarmDayAsyncTask(mDatabase.alarmDao()).execute(String.valueOf(id), day);
    }

    public List<String> getDaysList(int id) throws ExecutionException, InterruptedException {
        return new getDaysListAsyncTask(mDatabase.alarmDao()).execute(id).get();
    }

    // These static classes allow us to do database operations in a background thread.
    private static class DeleteAlarmAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncTaskDao;

        public DeleteAlarmAsyncTask(AlarmDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            mAsyncTaskDao.deleteAlarm(params[0]);
            return null;
        }
    }

    private static class PutAlarmAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncDao;

        public PutAlarmAsyncTask(AlarmDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(Alarm... alarms) {
            mAsyncDao.putAlarm(alarms[0]);
            Log.i(TAG_REPO, "PutAlarmAsyncTask called for alarm name: " + alarms[0].getName());
            return null;
        }
    }

    private static class PutAlarmDayAsyncTask extends AsyncTask<AlarmDay, Void, Void> {
        private AlarmDao mAsyncDao;

        public PutAlarmDayAsyncTask(AlarmDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(AlarmDay... days) {
            mAsyncDao.putAlarmDay(days[0]);
            Log.i(TAG_REPO, "PutAlarmDayAsyncTask called for day: " + days[0].getDayName());
            return null;
        }
    }

    private static class DeleteAlarmDayAsyncTask extends AsyncTask<String, Void, Void> {
        private AlarmDao mAsyncDao;

        public DeleteAlarmDayAsyncTask(AlarmDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected Void doInBackground(String... day) {
            mAsyncDao.deleteDay(Integer.parseInt(day[0]), day[1]);
            return null;
        }
    }

    private static class getDaysListAsyncTask extends AsyncTask<Integer, Void, List<String>> {
        private AlarmDao mAsyncDao;

        public getDaysListAsyncTask(AlarmDao dao) {
            mAsyncDao = dao;
        }

        @Override
        protected List<String> doInBackground(Integer... id) {
            Log.i(TAG_REPO, "Getting alarm days from id = " + id[0].toString());
            return mAsyncDao.getDaysList(id[0]);
        }
    }


}
