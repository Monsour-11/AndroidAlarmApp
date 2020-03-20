package com.android.monsoursaleh.missionalarm;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmsViewModel extends AndroidViewModel {
    private AlarmRepository mRepository;
    private LiveData<List<Alarm>> mAlarms;
    private LiveData<List<String>> mDays;

    public AlarmsViewModel(Application application) {
        super(application);
        // Get Instance of repository.
        mRepository = AlarmRepository
                .getRepository(application
                        .getApplicationContext());

        // Set the alarms list variable.
        mAlarms = mRepository.getAlarms();
        mDays = mRepository.getAllDays();
    }

    public void saveAlarm(Alarm alarm) {
        mRepository.addAlarm(alarm);
    }


    public void addAlarmDay(AlarmDay day) {
        mRepository.putAlarmDay(day);
    }

    public LiveData<List<Alarm>> getAlarms() {
        return mAlarms;
    }


    public LiveData<List<String>> getAlarmDays() {
        return mDays;
    }

    public LiveData<Alarm> getAlarm(String name) {
        return mRepository.getAlarm(name);
    }

    public LiveData<List<String>> getDays(int id) {
        return mRepository.getDays(id);
    }

    public List<String> getDaysList(int id) throws ExecutionException, InterruptedException {
        return mRepository.getDaysList(id);
    }


    public void deleteAlarm(Alarm alarm) {
        mRepository.deleteAlarm(alarm);
    }


    public void deleteDay(int id, String day) {
        // Update Alarm days from alarm with certain given id.
        mRepository.deleteDay(id, day);
    }
}
