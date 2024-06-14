//package com.example.oplogy;
//import com.google.maps.GeoApiContext;
//import com.google.maps.GeocodingApi;
//import com.google.maps.model.GeocodingResult;
//import com.google.type.LatLng;
//
//
//public class AddressConverter {
//    public static LatLng convertAddressToLatLng(String address) {
//        try {
//            // Google MapsのAPIキーを使用してGeoApiContextオブジェクトを作成します
//            GeoApiContext context = new GeoApiContext.Builder()
//                    .apiKey("AIzaSyBQ1Ak-I2NL5TP4K59ZI0VgzKk6HNZuusw") //GoogleMapsAPiのAPiキー
//                    .build();
//            // GeocodingApiを使用して住所を緯度経度に変換します
//            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
//            if (results != null && results.length > 0) {
//                // 位置情報（緯度と経度）を返します
//                return results[0].geometry.location;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 結果が存在しない場合やエラーが発生した場合は、nullを返します
//        return null;
//    }
//}