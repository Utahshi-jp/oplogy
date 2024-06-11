package com.example.oplogy;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Locale;


public class SetUpActivity extends FragmentActivity
        implements TimePickerDialog.OnTimeSetListener {

    private TextView startTime;
    private TextView endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String str = String.format(Locale.US, "%d:%d", hourOfDay, minute);

        startTime.setText(str);
        endTime.setText(str);

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }
}
