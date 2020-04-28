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
import android.widget.CompoundButton;
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
import java.text.DateFormat;
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
            mViewModel.getAlarm(getArguments().getString(ARG_ALARM_NAME)).observe(
                    getViewLifecycleOwner(), new Observer<Alarm>() {
                @Override
                public void onChanged(Alarm alarm) {
                    // Set alarm member variable.
                    mAlarm = alarm;

                    Log.i(TAG_ALARM_FRAG, "Filling in data from " + alarm.getName() + " alarm");

                    // Instantiate chips based on days in database corresponding
                    // to this alarm id.
                    updateDayChips(alarm);

                    // Set state of UI elements.
                    mAlarmSound.setText(alarm.getRingtone());
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
            Log.i(TAG_ALARM_FRAG, "Creating a new Alarm");

            // This instantiates the chips for choosing alarms days by default.
            updateDayChips(mAlarm);
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

                // This try/catch is for handling exceptions that the function throws.
                try {
                    updateAlarmDays();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                Log.i(TAG_ALARM_FRAG, "Saving the following alarm to database:\n" +
                        "Name: " + mAlarm.getName() + ", " + "Time: " +
                        DateFormat.getInstance().format(mAlarm.getTime()));
                mViewModel.saveAlarm(mAlarm);
                AlarmReceiver.setAlarm(getActivity(), mAlarm.getAlarmSound());
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

        mToggleVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAlarm.setVibrate(b);
                if (b) {
                    mToggleVibrate.setText(R.string.on);
                } else {
                    mToggleVibrate.setText(R.string.off);
                }
            }
        });

        mToggleSnooze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAlarm.setSnooze(b);
                if (b) {
                    mToggleSnooze.setText(R.string.on);
                } else {
                    mToggleSnooze.setText(R.string.off);
                }
            }
        });

        return binding.getRoot();
    }

    private void updateDayChips(Alarm alarm) {
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
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(AppCompatResources
                    .getColorStateList(getActivity(), R.color.chip_state));
            List<String> days = null;

            if (!isNewAlarm) {
                try {
                    days = mViewModel.getDaysList(alarm.getId());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                assert days != null;

                chip.setChecked(days.contains(day.substring(0, 3)));
                Log.i(TAG_ALARM_FRAG, String.format("Check %s chip = %b",
                        day.substring(0, 3), days.contains(day.substring(0,3 ))));
            }

            else {
                Log.i(TAG_ALARM_FRAG, "Default instantiation of chip days");
                chip.setChecked(true);
            }
            mChips.addView(chip);
        }
    }

    private void updateAlarmDays() throws ExecutionException, InterruptedException {
        // All alarms days of specific alarm.
        List<String> days;

        if (!isNewAlarm) {
            // Get the list of days if this is not a newly created alarm.
            days = mViewModel.getDaysList(mAlarm.getId());
        } else {
            days = null;
        }

        // For loop through all chips in layout.
        for (int i = 0; i < mChips.getChildCount(); i++) {
            Chip chip = (Chip)mChips.getChildAt(i);
            String text = chip.getText().toString();

            Log.i(TAG_ALARM_FRAG, String.format("Chip %s checked = %b, " +
                    "isNewAlarm = %b, !days.contains(%s) = %b", text, chip.isChecked(), isNewAlarm,
                    text, days != null && !days.contains(text)));

            // If a chip is checked and this is a new alarm, then add it to the days list.
            // If this is not a new alarm, then add a new day if this day is not in the
            // days list that corresponds with the alarm id.
            if ((chip.isChecked() && isNewAlarm) ||
                    chip.isChecked() && days != null && !days.contains(text)) {
                Log.i(TAG_ALARM_FRAG, "Adding " + text + " to alarm days for " +
                        mAlarm.getName() + " alarm.");
                AlarmDay day = new AlarmDay();
                day.setAlarmId(mAlarm.getId());
                day.setDay(getDayInt(text));
                day.setDayName(text);
                mViewModel.addAlarmDay(day);
            }

            // If a chip is in days list and it is no longer selected, then delete it.
            // This only applies for alarms that are not new.
            else if (!chip.isChecked() && days != null && days.contains(text)) {
                Log.i(TAG_ALARM_FRAG, "Deleting " + text + " from alarm days for " +
                        mAlarm.getName() + " alarm");
                mViewModel.deleteDay(mAlarm.getId(), chip.getText().toString());
            }
        }
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
        // If the request was to get data about the ringtone the user chose.
        if (requestCode == REQUEST_ALARM_SOUND) {
            Uri soundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String alarmSound = RingtoneManager.getRingtone(getActivity(), soundUri)
                    .getTitle(getActivity());
            mAlarmSound.setText(alarmSound);
            mAlarm.setRingtone(alarmSound);
            mAlarm.setAlarmSound(soundUri);
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
                .replace(R.id.fragment_container, fragment).commit();
    }
}
