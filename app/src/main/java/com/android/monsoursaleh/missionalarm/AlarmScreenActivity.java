package com.android.monsoursaleh.missionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class AlarmScreenActivity extends AppCompatActivity implements SensorEventListener {
    private static final String EXTRA_RINGTONE_URI = "ringtone_uri";
    private TextView mStepsTextView;
    private MaterialButton mButton;
    private SensorManager mSensorManager;
    private Sensor mStepDetector;
    private Ringtone mRingtone;
    private boolean isAlarmOn;
    private int mSteps = 0;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        if (Build.VERSION.SDK_INT >= 27) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        else {
            // Keep the screen on for the alarm.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                                 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                                 WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        // Get text view and button from the layout.
        mStepsTextView = findViewById(R.id.steps_counter);
        mStepsTextView.setText(String.valueOf(mSteps));
        mButton = findViewById(R.id.shut_alarm);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do something.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        isAlarmOn = true;

        // Get the ringtone and make it sound.
        mRingtone = RingtoneManager.getRingtone(this,
                (Uri)getIntent().getParcelableExtra(EXTRA_RINGTONE_URI));

        // Play the ringtone on a background thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Make the ringtone loop.
                if (Build.VERSION.SDK_INT < 28) {
                    while(isAlarmOn) {
                        mRingtone.play();
                    }
                }

                else {
                    mRingtone.setLooping(true);
                    mRingtone.play();
                }
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Do something.
        mSteps++;
        mStepsTextView.setText(String.valueOf(mSteps));
        if (mSteps == 10) {
            isAlarmOn = false;
            mRingtone.stop();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do something.
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }
}
