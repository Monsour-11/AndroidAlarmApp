package com.android.monsoursaleh.missionalarm;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.List;

public class AlarmsViewModel extends AndroidViewModel {
    private AlarmRepository mRepository;
    private MutableLiveData<List<Alarm>> mAlarms;
    private MutableLiveData<List<AlarmTime>> mTimes;
    private MutableLiveData<List<AlarmDay>> mDays;

    public AlarmsViewModel(Application application) {
        super(application);
        // Get Instance of repository.
        mRepository = AlarmRepository
                .getRepository(application
                        .getApplicationContext());

        // Set the alarms list variable.
        mAlarms = new MutableLiveData<>();
        mAlarms.setValue(mRepository.getAlarms().getValue());
        mTimes = new MutableLiveData<>();
        mTimes.setValue(mRepository.getAlarmTimes().getValue());
        mDays = new MutableLiveData<>();
        mDays.setValue(mRepository.getAlarmDays().getValue());
    }

    public void saveAlarm(Alarm alarm) {
        mRepository.changeName(alarm.getName(), alarm.getId());
        changeSound(alarm.getAlarmSound(), alarm.getId());
        changeSnooze(alarm.isSnooze(), alarm.getId());
        changeVibrate(alarm.isVibrate(), alarm.getId());
        mAlarms.setValue(mRepository.getAlarms().getValue());
    }

    public void addAlarm(Alarm alarm) {
        mRepository.addAlarm(alarm);
        mAlarms.setValue(mRepository.getAlarms().getValue());
    }

    public void addAlarmTime(AlarmTime time) {
        mRepository.putAlarmTime(time);
        mTimes.setValue(mRepository.getAlarmTimes().getValue());
    }

    public void addAlarmDay(AlarmDay day) {
        mRepository.putAlarmDay(day);
        mDays.setValue(mRepository.getAlarmDays().getValue());
    }

    public LiveData<List<Alarm>> getAlarms() {
        return mAlarms;
    }

    public LiveData<List<AlarmTime>> getAlarmTimes() {
        return mTimes;
    }

    public LiveData<List<AlarmDay>> getAlarmDays() {
        return mDays;
    }

    public LiveData<AlarmTime> getAlarmTime(int id) {
        return mRepository.getAlarmTime(id);
    }


    public LiveData<Alarm> getAlarm(String name) {
        return mRepository.getAlarm(name);
    }

    public LiveData<List<String>> getDays(int id) {
        return mRepository.getDays(id);
    }

    public void changeSound(String sound, int id) {
        mRepository.changeSound(sound, id);
    }

    public void changeSnooze(boolean isSnooze, int id) {
        mRepository.changeSnooze(isSnooze, id);
    }

    public void changeVibrate(boolean isVibrate, int id) {
        mRepository.changeVibrate(isVibrate, id);
    }

    public void deleteAlarm(Alarm alarm) {
        mRepository.deleteAlarm(alarm);
        mAlarms.setValue(mRepository.getAlarms().getValue());
    }

    public void deleteDay(AlarmDay day) {
        mRepository.deleteAlarmDay(day);
        mDays.setValue(mRepository.getAlarmDays().getValue());
    }
}
