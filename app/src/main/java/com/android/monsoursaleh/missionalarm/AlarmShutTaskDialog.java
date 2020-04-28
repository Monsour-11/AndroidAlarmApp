package com.android.monsoursaleh.missionalarm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AlarmShutTaskDialog extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.alarm_shut_task_dialog, null);


        AlertDialog.Builder builder = new
                AlertDialog.Builder(getActivity());

        builder.setView(v).setTitle(R.string.hint_alarm_shut)
                .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Save alarm shut settings.
            }
        }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Don't save settings and leave fragment.
            }
        });

        return builder.create();

    }

}
