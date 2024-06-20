package com.example.oplogy;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CreateRoot {
    private Context context;
    private AppDatabase db;

    public CreateRoot(MainActivity activity) {
        this.context = activity;
        this.db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "SetUpTable").build();
    }

    public void receiveData(List<MyDataClass> myDataList) {
        for (int i = 0; i < myDataList.size(); i++) {
            //希望時間帯の終了時刻から開始時刻を引いて希望時間帯の長さ(timezone)に入れる
            MyDataClass data = myDataList.get(i);
            List<Timestamp> firstDay = data.getFirstDay();
            Timestamp startTime = firstDay.get(0);
            Timestamp endTime = firstDay.get(1);
            Long timezone = endTime.getSeconds() - startTime.getSeconds();
            data.setTimezone(timezone);

            //TimeStampを日付に変換
            Date startDate = new Date(startTime.getSeconds() * 1000);
            Date endDate = new Date(endTime.getSeconds() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateString = sdf.format(startDate);
            String endDateString = sdf.format(endDate);

            // myDataList の中の data に追加する処理
            myDataList.get(i).setTimezone(timezone);
            myDataList.get(i).setStartDateString(startDateString);
            myDataList.get(i).setEndDateString(endDateString);

            // ログ出力
            Log.d("CreateRoot", "(index: " + i + ") timezone: " + myDataList.get(i).getTimezone());
            Log.d("CreateRoot", "(index: " + i + ") startDate: " + myDataList.get(i).getStartDateString());
            Log.d("CreateRoot", "(index: " + i + ") data: " + myDataList.get(i));
        }
        // timezoneを比較するComparator→timezoneが短い順に並べる
        Comparator<MyDataClass> comparator = new Comparator<MyDataClass>() {
            @Override
            public int compare(MyDataClass data1, MyDataClass data2) {
                return data1.getTimezone().compareTo(data2.getTimezone());
            }
        };
        // myDataListをtimezoneの値でソート
        Collections.sort(myDataList, comparator);
        // ソート後のmyDataListをログ出力
        for (int i = 0; i < myDataList.size(); i++) {
            Log.d("CreateRoot", "(index: " + i + ") timezone: " + myDataList.get(i).getTimezone());
            Log.d("CreateRoot", "(index: " + i + ") startDate: " + myDataList.get(i).getStartDateString());
            Log.d("CreateRoot", "(index: " + i + ") data: " + myDataList.get(i));

        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SetUpTableDao setUpTableDao = db.setUpTableDao();
                String startTime=setUpTableDao.getStartTime();
                Log.d("CreateRoot", "開始時間" + startTime);
            }
        });
    }
}
