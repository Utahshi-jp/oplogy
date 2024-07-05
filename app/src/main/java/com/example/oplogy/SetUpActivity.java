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
    private TextView textViewTeacherName;
    private TextView textViewStartPoint;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private TextView textViewStartBreakTime;
    private TextView textViewEndBreakTime;
    private TextView textViewTotalStudent;
    private int intIsDateSelected;
    private int intIsStartTimeSelected;

    String stringYear;
    String stringMonth;
    String stringDayOfMonth;


    String stringHourOfDay;
    String stringMinute;

    Button buttonFirstDay;
    Button buttonSecondDay;
    Button buttonThirdDay;
    Button buttonStartTimeButton;
    Button buttonEndTimeButton;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        int classId= getIntent().getIntExtra("classId", 100000);

        textViewTeacherName = findViewById(R.id.teacherName);                    //先生の名前
        textViewStartPoint = findViewById(R.id.startPoint);                      //開始地点

        buttonFirstDay = findViewById(R.id.setFirstDayButton);                    //1日目の日付
        buttonSecondDay = findViewById(R.id.setSecondDayButton);                  //2日目の日付
        buttonThirdDay = findViewById(R.id.setThirdDayButton);                    //3日目の日付

        buttonStartTimeButton = findViewById(R.id.startTimeSetButton);         //開始時刻を設定するボタン
        textViewStartTime = findViewById(R.id.startTime);                        //開始時刻を出力するTextView
        buttonEndTimeButton = findViewById(R.id.endTimeSetButton);             //終了時刻を設定するボタン
        textViewEndTime = findViewById(R.id.endTime);                            //終了時刻を出力するTextView

        RadioButton radioButtonTenMinute = findViewById(R.id.tenMinute);            //訪問間隔（10分）
        RadioButton radioButtonFifteenMinute = findViewById(R.id.fifteenMinute);    //訪問間隔（15分）
        RadioButton radioButtonThirtyMinute = findViewById(R.id.thirtyMinute);      //訪問間隔（30分）

        textViewStartBreakTime = findViewById(R.id.startBreakTime);              //休憩開始時刻
        textViewStartBreakTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        textViewEndBreakTime = findViewById(R.id.endBreakTime);                  //休憩終了時刻
        textViewEndBreakTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        textViewTotalStudent = findViewById(R.id.totalStudent);                  //クラスの人数

        ImageView toMain = findViewById(R.id.toMain);
        Button setUp = findViewById(R.id.setUpButton);                      //画面下の設定ボタン
        Button reset = findViewById(R.id.resetButton);

        toMain.setOnClickListener(view -> {
            Intent intent = new Intent(SetUpActivity.this,MainActivity.class); //main画面へ戻る処理
            startActivity(intent);
        });

        setUp.setOnClickListener(view -> {

            teacherName = textViewTeacherName.getText().toString(); //各変数に値を挿入
            Log.d(TAG, "Teacher Name: " + teacherName);
            startPoint = textViewStartPoint.getText().toString();
            Log.d(TAG, "Start Point: " + startPoint);
            Log.d(TAG, "First Day:" + firstDay);
            Log.d(TAG, "Second Day:" + secondDay);
            Log.d(TAG, "Third Day:" + thirdDay);
            Log.d(TAG, "Start Time" + startTime);
            Log.d(TAG, "End Time" + endTime);
            if (radioButtonTenMinute.isChecked()){                    //ラジオボタンの状態を取得
                intervalTime = "10";
            } else if (radioButtonFifteenMinute.isChecked()) {
                intervalTime = "15";
            } else if (radioButtonThirtyMinute.isChecked()) {
                intervalTime = "30";
            } else {
                intervalTime = "0";
            }
            Log.d(TAG, "Interval Time" + intervalTime);
            Log.d(TAG, "Start Break Time" + startBreakTime);
            Log.d(TAG, "End Break Time" + endBreakTime);
            totalStudent = Integer.parseInt(textViewTotalStudent.getText().toString()); //数値型に変更
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
                SharedPreferences sharedPreferences=getSharedPreferences("visitingDate",MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();

                //editorに値を渡す
                editor.putString("day1",firstDay);  //1日目
                editor.putString("day2",secondDay); //2日目
                editor.putString("day3",thirdDay);  //3日目

                editor.apply();

            });


        });

        //DatePicker用
        buttonFirstDay.setOnClickListener(v ->{
            intIsDateSelected = 1;  //ボタンの判別（Date）
            showDatePickerDialog(); //DatePickerの表示
        });

        buttonSecondDay.setOnClickListener(v ->{
            intIsDateSelected = 2;
            showDatePickerDialog();
        });

        buttonThirdDay.setOnClickListener(v ->{
            intIsDateSelected = 3;
            showDatePickerDialog();
        });

        //TimePicker用
        buttonStartTimeButton.setOnClickListener(v -> {
            intIsStartTimeSelected = 1; //ボタンの判別（Time）
            showTimePickerDialog();     //TimePickerの表示
        });

        buttonEndTimeButton.setOnClickListener(v -> {
            intIsStartTimeSelected = 2;
            showTimePickerDialog();
        });

        textViewStartBreakTime.setOnClickListener(v -> {
            intIsStartTimeSelected = 3;
            showTimePickerDialog();
        });

        textViewEndBreakTime.setOnClickListener(v -> {
            intIsStartTimeSelected = 4;
            showTimePickerDialog();
        });

        //リセットボタンの処理

        reset.setOnClickListener(v -> { //テキストとラジオボタンの選択を消去
            textViewTeacherName.setText("");
            textViewStartPoint.setText("");
            radioButtonTenMinute.setChecked(false);
            radioButtonFifteenMinute.setChecked(false);
            radioButtonThirtyMinute.setChecked(false);
            textViewStartBreakTime.setText("");
            textViewEndBreakTime.setText("");
            textViewTotalStudent.setText("");

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
        String str = String.format(Locale.JAPAN, "%02d/%02d",  month + 1, dayOfMonth); // TextViewに表示する日付の形式を設定

        if (intIsDateSelected == 1) {
            stringYear = String.valueOf(year);                                          //年
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1); //月
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);  //日
            firstDay = stringYear + stringMonth + stringDayOfMonth;                     //8桁の文字列を作成 例)20240604
            buttonFirstDay.setText(str);                                                //buttonにformatされた文字列を挿入

        } else if (intIsDateSelected == 2) {
            stringYear = String.valueOf(year);
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1);
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);
            secondDay = stringYear + stringMonth + stringDayOfMonth;
            buttonSecondDay.setText(str);


        } else if (intIsDateSelected == 3) {
            stringYear = String.valueOf(year);
            stringMonth = String.format(Locale.JAPAN, "%02d", month + 1);
            stringDayOfMonth = String.format(Locale.JAPAN, "%02d", dayOfMonth);
            thirdDay = stringYear + stringMonth + stringDayOfMonth;
            buttonThirdDay.setText(str);

        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String str = String.format(Locale.JAPAN, "%02d:%02d", hourOfDay, minute); // Textviewに保存する形式を設定

        if (intIsStartTimeSelected == 1) {
            stringHourOfDay = String.format("%02d", hourOfDay); //時
            stringMinute = String.format("%02d", minute);       //分
            startTime = stringHourOfDay + stringMinute;         //4桁の文字列を作成 例）0930
            textViewStartTime.setText(str);                     //textViewにformatされている文字列を挿入

        } else if (intIsStartTimeSelected == 2) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            endTime = stringHourOfDay + stringMinute;
            textViewEndTime.setText(str);

        } else if (intIsStartTimeSelected == 3) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            startBreakTime =stringHourOfDay + stringMinute;
            textViewStartBreakTime.setText("　" + str + "　");

        } else if (intIsStartTimeSelected == 4) {
            stringHourOfDay = String.format("%02d", hourOfDay);
            stringMinute = String.format("%02d", minute);
            endBreakTime = stringHourOfDay + stringMinute;
            textViewEndBreakTime.setText("　" + str + "　");
        }
    }

    private void showDatePickerDialog() { //DatePickerDialogを表示する
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() { // TimePickerDialogを表示する
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

}