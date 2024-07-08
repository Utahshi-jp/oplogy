package com.example.oplogy;

import static android.content.ContentValues.TAG;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SetUpActivity extends FragmentActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    String teacherName;
    String startPoint;
    String firstDay;
    String secondDay;
    String thirdDay;
    String startTime;
    String endTime;
    String intervalTime;
    String startBreakTime;
    String endBreakTime;
    int totalStudent;
    private TextView setTeacherName;
    private TextView setStartPoint;
    private TextView setStartTime;
    private TextView setEndTime;
    private TextView setStartBreakTime;
    private TextView setEndBreakTime;
    private TextView setTotalStudent;
    private int isDateSelected;
    private int isStartTimeSelected;

    String stringYear;
    String stringMonth;
    String stringDayOfMonth;


    String stringHourOfDay;
    String stringMinute;

    Button setFirstDay;
    Button setSecondDay;
    Button setThirdDay;
    Button setStartTimeButton;
    Button setEndTimeButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        int classId = getIntent().getIntExtra("classId", 100000);

        setTeacherName = findViewById(R.id.teacherName);                    //先生の名前
        setStartPoint = findViewById(R.id.startPoint);                      //開始地点

        setFirstDay = findViewById(R.id.setFirstDayButton);                    //1日目の日付
        setSecondDay = findViewById(R.id.setSecondDayButton);                  //2日目の日付
        setThirdDay = findViewById(R.id.setThirdDayButton);                    //3日目の日付

        setStartTimeButton = findViewById(R.id.startTimeSetButton);         //開始時刻を設定するボタン
        setStartTime = findViewById(R.id.startTime);                        //開始時刻を出力するTextView
        setEndTimeButton = findViewById(R.id.endTimeSetButton);             //終了時刻を設定するボタン
        setEndTime = findViewById(R.id.endTime);                            //終了時刻を出力するTextView

        RadioButton setTenMinute = findViewById(R.id.tenMinute);            //訪問間隔（10分）
        RadioButton setFifteenMinute = findViewById(R.id.fifteenMinute);    //訪問間隔（15分）
        RadioButton setThirtyMinute = findViewById(R.id.thirtyMinute);      //訪問間隔（30分）

        setStartBreakTime = findViewById(R.id.startBreakTime);              //休憩開始時刻
        setStartBreakTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        setEndBreakTime = findViewById(R.id.endBreakTime);                  //休憩終了時刻
        setEndBreakTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        setTotalStudent = findViewById(R.id.totalStudent);                  //クラスの人数

        ImageView toMain = findViewById(R.id.toMain);
        Button setUp = findViewById(R.id.setUpButton);                      //画面下の設定ボタン
        Button reset = findViewById(R.id.resetButton);

        toMain.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpActivity.this, MainActivity.class); //main画面へ戻る処理
            startActivity(intent);
        });

        setUp.setOnClickListener(view -> {

            teacherName = setTeacherName.getText().toString(); //各変数に値を挿入
            Log.d(TAG, "Teacher Name: " + teacherName);
            startPoint = setStartPoint.getText().toString();
            Log.d(TAG, "Start Point: " + startPoint);
            Log.d(TAG, "First Day:" + firstDay);
            Log.d(TAG, "Second Day:" + secondDay);
            Log.d(TAG, "Third Day:" + thirdDay);
            Log.d(TAG, "Start Time" + startTime);
            Log.d(TAG, "End Time" + endTime);
            if (setTenMinute.isChecked()) {                    //ラジオボタンの状態を取得
                intervalTime = "10";
            } else if (setFifteenMinute.isChecked()) {
                intervalTime = "15";
            } else if (setThirtyMinute.isChecked()) {
                intervalTime = "30";
            } else {
                intervalTime = "0";
            }
            Log.d(TAG, "Interval Time" + intervalTime);
            Log.d(TAG, "Start Break Time" + startBreakTime);
            Log.d(TAG, "End Break Time" + endBreakTime);
            totalStudent = Integer.parseInt(setTotalStudent.getText().toString()); //数値型に変更
            Log.d(TAG, "Total Student" + totalStudent);
            Log.d(TAG, "onClick: できてるよ");


            // データベースへの登録処理
            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                //roomのインスタンスを作成
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable")
                        .fallbackToDestructiveMigration()
                        .build();
                SetUpTableDao setUpTableDao = db.setUpTableDao();
                // Roomの操作を行う
                SetUpTable setUpTable = new SetUpTable(
                        teacherName,
                        startPoint,
                        startTime,
                        endTime,
                        intervalTime,
                        startBreakTime,
                        endBreakTime,
                        totalStudent,
                        classId
                );

                // 同じ名前のエントリが存在するかどうかを確認
                SetUpTable existingSetUpTable = setUpTableDao.findByName(teacherName);
                if (existingSetUpTable != null) {
                    // エントリが存在する場合は、そのエントリを更新
                    setUpTable.setId(existingSetUpTable.getId()); // 既存のIDを設定
                    setUpTableDao.update(setUpTable);

                    runOnUiThread(() -> Toast.makeText(SetUpActivity.this, "更新しました", Toast.LENGTH_SHORT).show());
                } else {
                    // エントリが存在しない場合は、新しいエントリを挿入
                    setUpTableDao.insertAll(setUpTable);
                    runOnUiThread(() -> Toast.makeText(SetUpActivity.this, "登録しました", Toast.LENGTH_SHORT).show());
                }
                //家庭訪問日を保存する共有プリファレンス
                SharedPreferences sharedPreferences = getSharedPreferences("visitingDate", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("day1", firstDay);
                editor.putString("day2", secondDay);
                editor.putString("day3", thirdDay);

                editor.apply();

            });


        });

        setFirstDay.setOnClickListener(v -> {
            isDateSelected = 1;
            showDatePickerDialog(); //DatePickerの表示
        });

        setSecondDay.setOnClickListener(v -> {
            isDateSelected = 2;
            showDatePickerDialog();
        });

        setThirdDay.setOnClickListener(v -> {
            isDateSelected = 3;
            showDatePickerDialog();
        });

        setStartTimeButton.setOnClickListener(v -> {
            isStartTimeSelected = 1; //ボタンの判別
            showTimePickerDialog(); //TimePickerの表示
        });

        setEndTimeButton.setOnClickListener(v -> {
            isStartTimeSelected = 2;
            showTimePickerDialog();
        });

        setStartBreakTime.setOnClickListener(v -> {
            isStartTimeSelected = 3;
            showTimePickerDialog();
        });

        setEndBreakTime.setOnClickListener(v -> {
            isStartTimeSelected = 4;
            showTimePickerDialog();
        });

        //リセットボタンの処理

        reset.setOnClickListener(v -> { //テキストとラジオボタンの選択を消去
            setTeacherName.setText("");
            setStartPoint.setText("");
            setTenMinute.setChecked(false);
            setFifteenMinute.setChecked(false);
            setThirtyMinute.setChecked(false);
            setStartBreakTime.setText("");
            setEndBreakTime.setText("");
            setTotalStudent.setText("");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "SetUpTable").build();
                SetUpTableDao setUpTableDao = db.setUpTableDao();
                setUpTableDao.deleteAll();
            });
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) { //Dateを成形する
        // DatePickerDialogで選択された日付を処理する
        String str = String.format(Locale.JAPAN, "%02d/%02d", month + 1, dayOfMonth); // TextViewに表示する日付の形式を設定

        if (isDateSelected == 1) {
            stringYear = String.valueOf(year);                                          //年
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1); //月
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);  //日
            firstDay = stringYear + stringMonth + stringDayOfMonth;
            setFirstDay.setText(str);
        } else if (isDateSelected == 2) {
            stringYear = String.valueOf(year);
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1);
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);
            secondDay = stringYear + stringMonth + stringDayOfMonth;
            setSecondDay.setText(str);


        } else if (isDateSelected == 3) {
            stringYear = String.valueOf(year);
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1);
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);
            thirdDay = stringYear + stringMonth + stringDayOfMonth;
            setThirdDay.setText(str);

        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String str = String.format(Locale.JAPAN, "%02d:%02d", hourOfDay, minute); // Textviewに保存する形式を設定

        if (isStartTimeSelected == 1) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            startTime = stringHourOfDay + stringMinute;
            setStartTime.setText(str);

        } else if (isStartTimeSelected == 2) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            endTime = stringHourOfDay + stringMinute;
            setEndTime.setText(str);

        } else if (isStartTimeSelected == 3) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            startBreakTime = stringHourOfDay + stringMinute;
            setStartBreakTime.setText("　" + str + "　");

        } else if (isStartTimeSelected == 4) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            endBreakTime = stringHourOfDay + stringMinute;
            setEndBreakTime.setText("　" + str + "　");
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() { // Dialogを表示する
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

}