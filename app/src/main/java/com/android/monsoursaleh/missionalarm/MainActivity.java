package com.android.monsoursaleh.missionalarm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_MAIN_ACTIVITY = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This view will hold the various fragments (screens).
        setContentView(R.layout.fragment_container);

        // By default, the fragment with the list of alarms is shown.
        putFragment(AlarmsFragment.getInstance());
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
}
