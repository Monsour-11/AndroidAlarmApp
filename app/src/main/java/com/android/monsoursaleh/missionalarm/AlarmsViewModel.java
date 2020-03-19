package com.android.monsoursaleh.missionalarm;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.Date;
import java.util.List;

public class AlarmsViewModel extends AndroidViewModel {
    private AlarmRepository mRepository;
    private MutableLiveData<List<Alarm>> mAlarms;
    private MutableLiveData<List<String>> mDays;

    public AlarmsViewModel(Application application) {
        super(application);
        // Get Instance of repository.
        mRepository = AlarmRepository
                .getRepository(application
                        .getApplicationContext());

        // Set the alarms list variable.
        mAlarms = new MutableLiveData<>();
        mAlarms.setValue(mRepository.getAlarms().getValue());
        mDays = new MutableLiveData<>();
        mDays.setValue(mRepository.getAllDays().getValue());
    }

    public void saveAlarm(Alarm alarm) {
        mRepository.addAlarm(alarm);
        mAlarms.postValue(mRepository.getAlarms().getValue());
        mDays.postValue(mRepository.getAllDays().getValue());
    }


    public void addAlarmDay(AlarmDay day) {
        mRepository.putAlarmDay(day);
        mAlarms.postValue(mRepository.getAlarms().getValue());
        mDays.postValue(mRepository.getAllDays().getValue());
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


    public void deleteAlarm(Alarm alarm) {
        mRepository.deleteAlarm(alarm);
        mAlarms.postValue(mRepository.getAlarms().getValue());
        mDays.postValue(mRepository.getAllDays().getValue());
    }


    public void deleteDay(int id, String day) {
        // Update Alarm days from alarm with certain given id.
        mRepository.deleteDay(id, day);
        mDays.postValue(mRepository.getAllDays().getValue());
    }
}
