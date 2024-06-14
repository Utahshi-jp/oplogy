package com.example.oplogy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GeoCoder {
    private Context context;

    public void processData(Map<String, Object> data, Context context) {
        try {
            this.context = context;

            //家庭訪問先の住所
            List<String> address = (List<String>) data.get("address");
            //家庭訪問の第一希望日(配列0が希望時間帯のはじめ、配列1がおわり)
            List<Timestamp> firstDay = (List<Timestamp>) data.get("firstDay");
            //出席番号
            Long studentNumber = (Long) data.get("studentNumber");
            // 住所を緯度経度に変換
            LatLng latLng = geocodeAddress(address.get(0));

            Log.d("FirestoreReception", "address: " + address.get(0));
            Log.d("FirestoreReception", "firstDay: " + firstDay.get(0));
            Log.d("FirestoreReception", "firstDay: " + firstDay.get(1));
            Log.d("FirestoreReception", "studentNumber: " + studentNumber);

            // 緯度経度をLogに出力
            Log.d("緯度経度", "latLng: "+latLng );
        } catch (NullPointerException e) {
            Log.e("NullPointerException", "getの中身がnull" + e);
        }
    }

    private LatLng geocodeAddress(String address) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressResult = addresses.get(0);
                double latitude = addressResult.getLatitude();
                double longitude = addressResult.getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            Log.e("GeocodingException", "Error geocoding address: " + address, e);
        }
        return null;
    }
}