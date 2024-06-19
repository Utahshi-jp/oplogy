package com.example.oplogy;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.List;

public class CreateRoot {
    public void receiveData(List<MyDataClass> myDataList) {

        // デバッグ用ログ
        for(MyDataClass data : myDataList){
            Log.d("CreateRoot", "data: "+ data.toString());
        }
            Log.d("CreateRoot", "myDataList[0]: " + myDataList.get(0).toString());

        for (MyDataClass data : myDataList) {
            Log.d("CreateRoot", "data: " + data.toString());
            Timestamp startTime = data.getFirstDay().get(0);
            Timestamp endTime = data.getFirstDay().get(1);
            Long timezone = endTime.getSeconds() - startTime.getSeconds();
            data.setTimezone(timezone);
            Log.d("CreateRoot", "timezone: " + timezone);
        }

    }
}
