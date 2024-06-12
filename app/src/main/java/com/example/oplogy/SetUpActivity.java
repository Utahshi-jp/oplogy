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
    private boolean isStartTimeSelected = true; // デフォルトはstartTimeを選択

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTimeSelected = true;
                showTimePickerDialog();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTimeSelected = false;
                showTimePickerDialog();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String str = String.format(Locale.US, "%d:%d", hourOfDay, minute); // Textviewに保存する形式を設定

        if (isStartTimeSelected) { //押した場所を判定して、押したほうにだけ挿入する
            startTime.setText(str);
        } else {
            endTime.setText(str);
        }
    }

    private void showTimePickerDialog() { // Dialogを表示する
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}