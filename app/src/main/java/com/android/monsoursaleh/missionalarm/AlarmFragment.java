package com.android.monsoursaleh.missionalarm;

import android.content.Intent;
import android.content.MutableContextWrapper;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.android.monsoursaleh.missionalarm.databinding.FragmentAlarmBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmFragment extends Fragment {
    private static final int REQUEST_ALARM_SOUND = 0;
    private static final String ARG_ALARM_NAME = "ARG_ALARM_NAME";
    private Alarm alarm;
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
        mViewModel = ViewModelProviders.of(this).get(AlarmsViewModel.class);

        // If an existing alarm was clicked from the list.
        if (getArguments() != null) {
            // Get the alarm that was clicked.
            alarm = mViewModel.getAlarm(getArguments().getString(ARG_ALARM_NAME)).getValue();
        } else {
            // Create new Alarm object.
            isNewAlarm = true;
            alarm = new Alarm();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.add_alarm_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
        getActivity().findViewById(R.id.add_alarm_button).setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save state of fragment before activity is destroyed.
        outState.putString(ARG_ALARM_NAME, alarm.getName());
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
            chip.setChecked(isNewAlarm || mViewModel.getDays(alarm.getId()).getValue()
                    .contains(day.substring(0, 3)));
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(getResources()
                    .getColorStateList(R.color.chip_state));
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
        if (!isNewAlarm) {
            time.setTime(alarm.getTime());
        }

        // Runs different method depending on SDk version.
        if (Build.VERSION.SDK_INT < 23) {
            mTime.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
            mTime.setCurrentMinute(time.get(Calendar.MINUTE));
        } else {
            mTime.setHour(time.get(Calendar.HOUR_OF_DAY));
            mTime.setMinute(time.get(Calendar.MINUTE));
        }


        // Set listeners on both buttons.
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish activity.
                exitFragment();
            }
        });

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* save alarm to database and
                go back to previous activity.
                 */
                alarm.setName(mName.getText().toString());

                // Do database operations in separate thread.
                new Thread(new Runnable() {
                   @Override
                   public void run() {
                       updateAlarmDays();
                       mViewModel.saveAlarm(alarm);
                   }
               }).start();

               exitFragment();
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete alarm from database.
                if (!isNewAlarm) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mViewModel.deleteAlarm(alarm);
                        }
                    }).start();
                }
                exitFragment();
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
                    alarm.setVibrate(true);
                } else {
                    mToggleVibrate.setText(R.string.off);
                    alarm.setVibrate(false);
                }
            }
        });

        mToggleSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mToggleSnooze.isChecked()) {
                    mToggleSnooze.setText(R.string.on);
                    alarm.setSnooze(true);
                } else {
                    mToggleSnooze.setText(R.string.off);
                    alarm.setSnooze(false);
                }
            }
        });

        return binding.getRoot();
    }

    private void updateAlarmDays() {
        // All alarms days of specific alarm.
        List<String> days = mViewModel.getDays(alarm.getId()).getValue();

        // For loop through all chips in layout.
        for (int i = 0; i < mChips.getChildCount(); i++) {
            Chip chip = (Chip)mChips.getChildAt(i);

            // Adds new day to alarm if clicked day is not there already.
            boolean condition = days != null ? !days.contains(chip.getText().toString()):isNewAlarm;
            if (chip.isChecked() && condition) {
                AlarmDay day = new AlarmDay();
                String text = ((Chip) mChips.getChildAt(i)).getText().toString();
                day.setAlarmId(alarm.getId());
                day.setDay(getDayInt(text));
                day.setDayName(text);
                mViewModel.addAlarmDay(day);
            }

            // This deletes an alarm day since day is no longer selected.
            else if (!chip.isChecked() && !isNewAlarm && days.contains(chip.getText().toString())) {
                mViewModel.deleteDay(alarm.getId(), chip.getText().toString());
            }
        }
    }


    private void exitFragment() {
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
            alarm.setAlarmSound(alarmSound);
        }
    }

    private void openAlarmTaskDialog() {
        AlarmShutTaskDialog dialog = new AlarmShutTaskDialog();
        dialog.show(getActivity().getSupportFragmentManager(),
                "Task Dialog");
    }
}
