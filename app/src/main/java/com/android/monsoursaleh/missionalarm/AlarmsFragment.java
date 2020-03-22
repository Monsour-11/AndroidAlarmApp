package com.android.monsoursaleh.missionalarm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.monsoursaleh.missionalarm.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.google.android.material.appbar.AppBarLayout;

public class AlarmsFragment extends Fragment {
    private final static String TAG_ALARMS_FRAG = "AlarmsFragment";
    private AlarmsViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mButton;

    // Static method that allows main activity to get an instance of this fragment.
    public static AlarmsFragment getInstance() {
        return new AlarmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get reference to shared view model.
        mViewModel = new ViewModelProvider(requireActivity()).get(AlarmsViewModel.class);

        // Set observer on list of alarms.
        mViewModel.getAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                // Update the adapter of the recycler view.
                if (alarms != null) {
                    Log.i(TAG_ALARMS_FRAG, "Update Alarms List: \n");
                    for (Alarm alarm : alarms) {
                        Log.i(TAG_ALARMS_FRAG, alarm.getId() + ", " + alarm.getName() + ", " +
                                DateFormat.getInstance().format(alarm.getTime()) + "\n");
                    }
                }
                mAdapter.updateAlarmsList(alarms);
                mAdapter.notifyDataSetChanged();
            }
        });

        // Set observer on alarm days.
        mViewModel.getAlarmDays().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                // Update the adapter of the recycler view.
                mAdapter.notifyDataSetChanged();
                if (strings != null) {
                    Log.i(TAG_ALARMS_FRAG, "Days list updated");
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup parent,
                             Bundle savedInstanceState) {
        ActivityMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_main,
                                                              parent, false);

        // Get recycler view, set adapter, linear layout manager, and item decoration.
        mRecyclerView = binding.recyclerView;
        mAdapter = new AlarmAdapter(mViewModel.getAlarms().getValue());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new RecyclerViewItemDecor(
                getResources().getDrawable(R.drawable.divider));
        mRecyclerView.addItemDecoration(itemDecoration);

        // Get tabs on top of main screen and add alarm button.
        mTabLayout = binding.tabs;
        mButton = binding.addAlarmButton;

        // Listen for when a tab is clicked on top of the main screen.
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

        // Listen for when the add alarm button is click on the bottom of the screen.
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get instance of alarm fragment
                AlarmFragment alarmFragment = AlarmFragment.newInstance();

                // Replace current fragment with an alarm fragment.
                replaceFragment(alarmFragment);
            }
        });

        return binding.getRoot();
    }


    // ViewHolder for alarms List.
    private class AlarmHolder extends RecyclerView.ViewHolder {
        private TextView mAlarmTitle;
        private TextView mAlarmTime;
        private TextView mAlarmDays;
        private SwitchMaterial mSwitch;
        private FrameLayout mItemView;

        AlarmHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            // This calls the constructor for parent class passing in layout view.
            super(DataBindingUtil.inflate(inflater, viewType, parent, false).getRoot());

            // Get the components from the layout.
            mAlarmTime = itemView.findViewById(R.id.alarm_time);
            mAlarmTitle = itemView.findViewById(R.id.alarm_label);
            mAlarmDays = itemView.findViewById(R.id.alarm_days);
            mSwitch = itemView.findViewById(R.id.toggle_alarm);
            mItemView = itemView.findViewById(R.id.alarm_item_view);
        }

        public void bind(Alarm alarm) throws ExecutionException, InterruptedException {
            mAlarmTime.setText(DateFormat.getTimeInstance()
                    .format(alarm.getTime()));
            mAlarmTitle.setText(alarm.getName());
            mSwitch.setChecked(true);
            String DaysText = "";
            for (String day : mViewModel.getDaysList(alarm.getId())) {
                DaysText.concat(day.substring(0, 3) + " ");
            }
            mAlarmDays.setText(DaysText);

            // Set listener on this particular item in the list.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get instance of Alarm Fragment.
                    AlarmFragment alarmFragment = AlarmFragment.newInstance();

                    // This will pass the name of the alarm clicked to Alarm Fragment.
                    Bundle args = new Bundle();
                    args.putString("ARG_ALARM_NAME", mAlarmTitle.getText().toString());
                    alarmFragment.setArguments(args);

                    Log.i(TAG_ALARMS_FRAG, "Alarm of name = " +
                            mAlarmTitle.getText().toString() + " clicked");

                    // Replace current fragment with an alarm fragment.
                    replaceFragment(alarmFragment);
                }
            });
        }
    }

    private void replaceFragment(Fragment fragment) {
        // Replace the current fragment in container with an Alarm Fragment.
        // Add current fragment to back stack so that when back button is
        // pressed, it will go back to alarms fragment.
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    // Adapter for alarms.
    public class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private List<Alarm> mAlarms;
        public AlarmAdapter(List<Alarm> alarms) {
            mAlarms = alarms;
        }

        // Updates alarms list whenever there is a change.
        public void updateAlarmsList(List<Alarm> updatedAlarms) {
            mAlarms = updatedAlarms;
        }

        // Creates an Alarm Holder to hold a new alarm.
        @NonNull
        @Override
        public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AlarmHolder(LayoutInflater.from(getActivity().getApplicationContext()),
                    parent, R.layout.alarm_item);
        }

        @Override
        public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {
            try {
                holder.bind(mAlarms.get(position));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
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
