package com.example.oplogy;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CreateRoot {
    public void receiveData(List<MyDataClass> myDataList) {

        for (int i = 0; i < myDataList.size(); i++) {
            MyDataClass data = myDataList.get(i);
            List<Timestamp> firstDay = data.getFirstDay();
            Timestamp startTime = firstDay.get(0);
            Timestamp endTime = firstDay.get(1);
            Long timezone = endTime.getSeconds() - startTime.getSeconds();
            data.setTimezone(timezone);

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
        // timezoneを比較するComparatorを作成
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
    }
}