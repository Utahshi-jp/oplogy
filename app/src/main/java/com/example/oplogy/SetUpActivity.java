package com.example.oplogy;

import static android.content.ContentValues.TAG;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Locale;


public class SetUpActivity extends FragmentActivity
        implements TimePickerDialog.OnTimeSetListener {

    String teacherName;
    String startPoint;
    String startTime;
    String endTime;
    String breakTime;
    int totalStudent;
    private TextView setTeacherName;
    private TextView setStartPoint;
    private TextView setStartTime;
    private TextView setEndTime;
    private TextView setBreakTime;
    private RadioButton setTenMinute;
    private RadioButton setFifteenMinute;
    private RadioButton setThirtyMinute;
    private TextView setTotalStudent;
    private Button setUp;
    private boolean isStartTimeSelected = true; // デフォルトはstartTimeを選択

    String startHourOfDay;
    String startMinute;
    String endHourOfDay;
    String endMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        setTeacherName = findViewById(R.id.teacherName);
        setStartPoint = findViewById(R.id.startPoint);
        setStartTime = findViewById(R.id.startTime);
        setEndTime = findViewById(R.id.endTime);
        setBreakTime = findViewById(R.id.breakTime);
        setTenMinute = findViewById(R.id.tenMinute);
        setFifteenMinute = findViewById(R.id.fifteenMinute);
        setThirtyMinute = findViewById(R.id.thirtyMinute);
        setTotalStudent = findViewById(R.id.totalStudent);

        setUp = findViewById(R.id.setUpButton);


        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                teacherName = setTeacherName.getText().toString();
                Log.d(TAG, "Teacher Name: " + teacherName);
                startPoint = setStartPoint.getText().toString();
                Log.d(TAG, "Start Point: " + startPoint);
                startTime = startHourOfDay + startMinute;
                Log.d(TAG, "Start Time: " + startTime);
                endTime = endHourOfDay + endMinute;
                Log.d(TAG, "End Time: " + endTime);
                breakTime = setBreakTime.getText().toString();
                Log.d(TAG, "Break Time: " + breakTime);
                totalStudent = Integer.parseInt(setTotalStudent.getText().toString());
                Log.d(TAG, "onClick: できてるよ");



            }
        });
        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTimeSelected = true;
                showTimePickerDialog();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTimeSelected = false;
                showTimePickerDialog();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String str = String.format(Locale.US, "%d:%d", hourOfDay, minute); // Textviewに保存する形式を設定

        if (isStartTimeSelected) { //押した場所を判定して、押したほうにだけ挿入する
            startHourOfDay = String.format("%02d", hourOfDay);
            startMinute = String.format("%02d", minute);
            setStartTime.setText(str);


        } else {
            endHourOfDay = String.format("%02d", hourOfDay);
            endMinute = String.format("%02d", minute);
            setEndTime.setText(str);
        }
    }

    private void showTimePickerDialog() { // Dialogを表示する
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}