package com.example.oplogy;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

public class CreateRoot {
    private String address;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private long studentNumber;
    private LatLng latLng;

    public void receiveData(String address, Timestamp startTimestamp, Timestamp endTimestamp, long studentNumber, LatLng latLng) {
        this.address = address;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.studentNumber = studentNumber;
        this.latLng = latLng;

        // 受け取ったデータを利用してログを出力
        Log.d("CreateRoot", "address: " + address);
        Log.d("CreateRoot", "startTimestamp: " + startTimestamp);
        Log.d("CreateRoot", "endTimestamp: " + endTimestamp);
        Log.d("CreateRoot", "studentNumber: " + studentNumber);
        Log.d("CreateRoot", "latLng: " + latLng);
    }
}