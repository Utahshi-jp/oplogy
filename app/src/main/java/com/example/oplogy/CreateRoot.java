package com.example.oplogy;

import android.util.Log;

import java.util.List;

public class CreateRoot {
    public void receiveData(List<MyDataClass> myDataList) {

        // デバッグ用ログ
        for(MyDataClass data : myDataList){
            Log.d("CreateRoot", "data: "+ data.toString());
        }
            Log.d("CreateRoot", "myDataList[0]: " + myDataList.get(0).toString());


    }
}