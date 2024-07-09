package com.example.oplogy;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePick extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstantState) {

        //デフォルトのタイムゾーンおよびロケールを使用してカレンダを取得
        final Calendar c = Calendar.getInstance();
        int yearInt = c.get(Calendar.YEAR);
        int monthInt = c.get(Calendar.MONTH);
        int dayInt = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), yearInt, monthInt, dayInt);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int yearInt, int monthInt, int dayInt) {

    }
}

