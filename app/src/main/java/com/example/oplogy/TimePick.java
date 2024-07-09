package com.example.oplogy;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePick extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) { // TimePeckerの生成
        final Calendar c = Calendar.getInstance();
        int hourInt = c.get(Calendar.HOUR_OF_DAY);
        int minuteInt = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) getActivity(), hourInt, minuteInt, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDayInt, int minuteInt) {
    }
}