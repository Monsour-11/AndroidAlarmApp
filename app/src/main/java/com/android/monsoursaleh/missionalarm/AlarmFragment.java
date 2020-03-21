package com.android.monsoursaleh.missionalarm;

import android.content.Intent;
import android.content.MutableContextWrapper;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.monsoursaleh.missionalarm.databinding.FragmentAlarmBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmFragment extends Fragment {
    private static final String TAG_ALARM_FRAG = "AlarmFragment";
    private static final int REQUEST_ALARM_SOUND = 0;
    private static final String ARG_ALARM_NAME = "ARG_ALARM_NAME";
    private Alarm mAlarm;
    private boolean isNewAlarm = false;
    private AlarmsViewModel mViewModel;
    private TimePicker mTime;
    private TextInputEditText mName;
    private TextView mAlarmSound;
    private TextView mVibrateOption;
    private TextView mSnoozeOption;
    private SwitchMaterial mToggleVibrate;
    private SwitchMaterial mToggleSnooze;
    private ChipGroup mChips;


    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get reference to view model.
        mViewModel = new ViewModelProvider(requireActivity()).get(AlarmsViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save state of fragment before activity is destroyed.
        outState.putString(ARG_ALARM_NAME, mAlarm.getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup parent,
                             Bundle savedInstanceState) {
        // Get layout binding.
        FragmentAlarmBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_alarm,
                        parent, false);

        // Get all fields required for alarm.
        mTime = binding.timePicker;
        mName = binding.alarmName;
        mAlarmSound = binding.soundName;
        mToggleSnooze = binding.toggleSnooze;
        mToggleVibrate = binding.toggleVibrate;

        // Get ChipGroup.
        mChips = binding.chipGroup;
        mChips.setChipSpacing(16);

        // If an existing alarm was clicked from the list.
        if (getArguments() != null) {
            // Get the alarm that was clicked.
            mViewModel.getAlarm(getArguments().getString(ARG_ALARM_NAME)).observe(getActivity(),
                    new Observer<Alarm>() {
                @Override
                public void onChanged(Alarm alarm) {
                    // Set alarm member variable.
                    mAlarm = alarm;

                    // Inflate chips into Chip group with days of week.
                    for (String day : getResources().getStringArray(R.array.days_of_week)) {
                        // Add chip to chip group.
                        Chip chip = new Chip(getActivity(), null,
                                R.style.Widget_MaterialComponents_Chip_Filter);

                        ChipGroup.LayoutParams lp = new ChipGroup.LayoutParams
                                (ChipGroup.LayoutParams.WRAP_CONTENT,
                                        ChipGroup.LayoutParams.WRAP_CONTENT);
                        chip.setText(day.substring(0, 3));
                        chip.setId(View.generateViewId());
                        chip.setLayoutParams(lp);
                        chip.setClickable(true);
                        chip.setCheckable(true);
                        List<String> days = null;
                        int id = alarm.getId();
                        try {
                            days = mViewModel.getDaysList(id);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        assert days != null;
                        chip.setChecked(days.contains(day.substring(0, 3)));

                        chip.setCheckedIconVisible(false);
                        chip.setChipBackgroundColor(AppCompatResources
                                .getColorStateList(getActivity(), R.color.chip_state));
                        mChips.addView(chip);
                    }

                    // Set state of UI elements.
                    mAlarmSound.setText(alarm.getAlarmSound());
                    mToggleSnooze.setChecked(alarm.isSnooze());
                    mToggleVibrate.setChecked(alarm.isVibrate());
                    mName.setText(alarm.getName());

                    // Instantiate calendar object.
                    Calendar time = Calendar.getInstance();

                    // If alarm is not new, use stored alarm time.
                    time.setTime(alarm.getTime());

                    // Runs different method depending on SDk version.
                    if (Build.VERSION.SDK_INT < 23) {
                        mTime.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
                        mTime.setCurrentMinute(time.get(Calendar.MINUTE));
                    } else {
                        mTime.setHour(time.get(Calendar.HOUR_OF_DAY));
                        mTime.setMinute(time.get(Calendar.MINUTE));
                    }
                }
            });

        } else {
            // Create new Alarm object.
            isNewAlarm = true;
            mAlarm = new Alarm();
        }


        // Set listeners on both buttons.
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish activity.
                replaceFragment(AlarmsFragment.getInstance());
            }
        });

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* save alarm to database and
                go back to previous activity.
                 */
                Log.i(TAG_ALARM_FRAG, "OK button clicked saving alarm...");
                mAlarm.setName(mName.getText().toString());
                mAlarm.setVibrate(mToggleVibrate.isChecked());
                mAlarm.setSnooze(mToggleSnooze.isChecked());
                Calendar time = Calendar.getInstance();

                time.set(Calendar.HOUR_OF_DAY, Build.VERSION.SDK_INT < 23 ?
                        mTime.getCurrentHour(): mTime.getHour());

                time.set(Calendar.MINUTE, Build.VERSION.SDK_INT < 23 ?
                        mTime.getCurrentMinute(): mTime.getMinute());
                mAlarm.setTime(time.getTime());
                updateAlarmDays();
                mViewModel.saveAlarm(mAlarm);
               replaceFragment(AlarmsFragment.getInstance());
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete alarm from database.
                if (!isNewAlarm) {
                    List<String> days = null;
                    try {
                        days = mViewModel.getDaysList(mAlarm.getId());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    mViewModel.deleteAlarm(mAlarm);

                    // Delete all days associated with that alarm.
                    assert days != null;
                    for(String day : days) {
                        mViewModel.deleteDay(mAlarm.getId(), day);
                    }
                }
                replaceFragment(AlarmsFragment.getInstance());
            }
        });


        binding.alarmSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch ringtone picker activity.
                Intent intent = Intent.createChooser(new Intent(RingtoneManager
                        .ACTION_RINGTONE_PICKER), "Alarm Sound");
                startActivityForResult(intent, REQUEST_ALARM_SOUND);
            }
        });

        mToggleVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToggleVibrate.isChecked()) {
                    mToggleVibrate.setText(R.string.on);
                    mAlarm.setVibrate(true);
                } else {
                    mToggleVibrate.setText(R.string.off);
                    mAlarm.setVibrate(false);
                }
            }
        });

        mToggleSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToggleSnooze.isChecked()) {
                    mToggleSnooze.setText(R.string.on);
                    mAlarm.setSnooze(true);
                } else {
                    mToggleSnooze.setText(R.string.off);
                    mAlarm.setSnooze(false);
                }
            }
        });

        return binding.getRoot();
    }

    private void updateAlarmDays() {
        // All alarms days of specific alarm.
        List<String> days = mViewModel.getDays(mAlarm.getId()).getValue();

        // For loop through all chips in layout.
        for (int i = 0; i < mChips.getChildCount(); i++) {
            Chip chip = (Chip)mChips.getChildAt(i);

            // Adds new day to alarm if clicked day is not there already.
            boolean dayNotFound = days != null && !days.contains(chip.getText().toString());
            if (chip.isChecked() && dayNotFound) {
                AlarmDay day = new AlarmDay();
                String text = ((Chip) mChips.getChildAt(i)).getText().toString();
                day.setAlarmId(mAlarm.getId());
                day.setDay(getDayInt(text));
                day.setDayName(text);
                mViewModel.addAlarmDay(day);
            }

            // This deletes an alarm day since day is no longer selected.
            else if (!chip.isChecked() && days != null && days.contains(chip.getText().toString())) {
                mViewModel.deleteDay(mAlarm.getId(), chip.getText().toString());
            }
        }
    }


    private void exitFragment() {
        Log.i(TAG_ALARM_FRAG, "Leaving Alarm Fragment");
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private int getDayInt(String dayName) {
        // Default value is the current day.
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch(dayName) {
            case "Sun":
                day = Calendar.SUNDAY;
                break;
            case "Sat":
                day = Calendar.SATURDAY;
                break;
            case "Mon":
                day = Calendar.MONDAY;
                break;
            case "Tue":
                day = Calendar.TUESDAY;
                break;
            case "Wed":
                day = Calendar.WEDNESDAY;
                break;
            case "Thu":
                day = Calendar.THURSDAY;
                break;
            case "Fri":
                day = Calendar.FRIDAY;
                break;
        }
        return day;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ALARM_SOUND) {
            Uri soundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String alarmSound = RingtoneManager.getRingtone(getActivity(), soundUri)
                    .getTitle(getActivity());
            mAlarmSound.setText(alarmSound);
            mAlarm.setAlarmSound(alarmSound);
        }
    }

    private void openAlarmTaskDialog() {
        AlarmShutTaskDialog dialog = new AlarmShutTaskDialog();
        dialog.show(getActivity().getSupportFragmentManager(),
                "Task Dialog");
    }

    private void replaceFragment(Fragment fragment) {
        // Replace the current fragment in container with an Alarm Fragment.
        // Add current fragment to back stack so that when back button is
        // pressed, it will go back to alarms fragment.
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}
