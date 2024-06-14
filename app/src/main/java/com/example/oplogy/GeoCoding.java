package com.example.oplogy;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GeoCoding {
    private GeoApiContext geoApiContext;
    public void processData(Map<String, Object> data) {

        try {
            // Google Cloud Platformで作成したAPIキーを設定します
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyBQ1Ak-I2NL5TP4K59ZI0VgzKk6HNZuusw")
                    .build();
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
            Log.d("Geocodingtry", "tryに入った");
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
            Log.d("GeocodingResult", "Results: " + Arrays.toString(results));
            if (results != null && results.length > 0) {
                return new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
            }
        } catch (Exception e) {
            Log.e("GeocodingException", "Error geocoding address: " + address, e);
        }
        return null;
    }

}
