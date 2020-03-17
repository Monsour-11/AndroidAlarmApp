package com.android.monsoursaleh.missionalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.monsoursaleh.missionalarm.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.Provider;
import java.text.DateFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AlarmsViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAdapter;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private FloatingActionButton mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get view model
        mViewModel = ViewModelProviders.of(this).get(AlarmsViewModel.class);

        // Set observer on list of alarms.
        mViewModel.getAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                // Update the adapter of the recycler view.
                mAdapter.updateAlarmsList(alarms);
            }
        });

        // Set observer on alarm times and days.
        mViewModel.getAlarmTimes().observe(this, new Observer<List<AlarmTime>>() {
            @Override
            public void onChanged(List<AlarmTime> alarmTimes) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getAlarmDays().observe(this, new Observer<List<AlarmDay>>() {
            @Override
            public void onChanged(List<AlarmDay> alarmDays) {
                mAdapter.notifyDataSetChanged();
            }
        });

        // Get TabLayout and set listener for when tabs are clicked.
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Get recycler view, set adapter, and layout manager.
        mRecyclerView = binding.recyclerView;
        mAdapter = new AlarmAdapter(mViewModel.getAlarms().getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mToolbar = binding.toolbar;
        mTabLayout = binding.tabs;
        mButton = binding.addAlarmButton;

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() == "Metrics") {
                    // Switch to metrics fragment.
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Get button and set listener.
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start alarm fragment.
                putFragment(AlarmFragment.newInstance());
            }
        });
    }



    private void putFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            // Add the fragment to container and schedule commit.
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            // Replace the fragment.
            fm.beginTransaction().replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // ViewHolder for alarms List.
    private class AlarmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAlarmTitle;
        private TextView mAlarmTime;
        private TextView mAlarmDays;
        private SwitchMaterial mSwitch;

        AlarmHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(DataBindingUtil.inflate(inflater, viewType,
                    parent, false).getRoot());
            mAlarmTime = itemView.findViewById(R.id.alarm_time);
            mAlarmTitle = itemView.findViewById(R.id.alarm_label);
            mAlarmDays = itemView.findViewById(R.id.alarm_days);
            mSwitch = itemView.findViewById(R.id.toggle_alarm);
        }

        public void bind(Alarm alarm) {
            mAlarmTime.setText(DateFormat.getTimeInstance()
                    .format(AlarmsHelper.toDate(mViewModel
                            .getAlarmTime(alarm.getId()).getValue())));
            mAlarmTitle.setText(alarm.getName());
            mSwitch.setChecked(true);
            String DaysText = "";
            for (String day : mViewModel.getDays(alarm.getId()).getValue()) {
                DaysText.concat(day.substring(0, 3) + " ");
            }
            mAlarmDays.setText(DaysText);
        }

        public void onClick(View v) {
            // Open fragment that was clicked.
            AlarmFragment fragment = AlarmFragment.newInstance();
            Bundle args = new Bundle();
            args.putString("ARG_ALARM_NAME", mAlarmTitle.getText().toString());
            fragment.setArguments(args);
            putFragment(fragment);
        }
    }

    // Adapter for alarms.
    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private List<Alarm> mAlarms;
        public AlarmAdapter(List<Alarm> alarms) {
            mAlarms = alarms;
        }

        // Updates alarms list whenever there is a change.
        public void updateAlarmsList(List<Alarm> updatedAlarms) {
            mAlarms = updatedAlarms;
            mAdapter.notifyDataSetChanged();
        }

        // Creates an Alarm Holder to hold a new alarm.
        @NonNull
        @Override
        public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AlarmHolder(getLayoutInflater(), parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {
            holder.bind(mAlarms.get(position));
        }

        @Override
        public int getItemCount() {
            if (mAlarms == null) {
                return 0;
            } else {
                return mAlarms.size();
            }
        }
    }
}
