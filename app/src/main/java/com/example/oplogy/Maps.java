package com.example.oplogy;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.oplogy.databinding.MapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maps extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    //GoogleMapAPiで使用可能な色
    private static final int[] COLORS = new int[]{Color.parseColor("#007FFF"), // HUE_AZURE
            Color.parseColor("#0000FF"), // HUE_BLUE
            Color.parseColor("#00FFFF"), // HUE_CYAN
            Color.parseColor("#00FF00"), // HUE_GREEN
            Color.parseColor("#FF00FF"), // HUE_MAGENTA
            Color.parseColor("#FFA500"), // HUE_ORANGE
            Color.parseColor("#FF0000"), // HUE_RED
            Color.parseColor("#FF007F"), // HUE_ROSE
            Color.parseColor("#8A2BE2"), // HUE_VIOLET
            Color.parseColor("#FFFF00") // HUE_YELLOW
    };
    private final List<LatLng> latLngList = new ArrayList<>();
    private final List<String> nameList = new ArrayList<>();
    private final List<Integer> colorList = new ArrayList<>();
    private final Map<String, Runnable> dateMap = new HashMap<>();
    ImageView backMain;
    private GoogleMap mMap;
    private LinearLayout locationsName;
    private int colorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // バインディングの設定
        com.example.oplogy.databinding.MapsBinding binding = MapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // マップフラグメントの設定
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 各UI要素の設定
        backMain = findViewById(R.id.BackMain);
        backMain.setOnClickListener(this);

        locationsName = findViewById(R.id.locationsName);

        // スピナーの設定
        String dateDataString = formatDate(getSharedPreferencesData(0)) + "/" + formatDate(getSharedPreferencesData(1)) + "/" + formatDate(getSharedPreferencesData(2));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] dates = dateDataString.split("/");
        for (String date : dates) {
            adapter.add(date);
        }

        // 各日付に対応するRunnableを設定する
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            String dayString = getSharedPreferencesData(i);
            String formattedDayString = formatDate(dayString);
            dateMap.put(formattedDayString, () -> loadMapAndNames(createlocationData(finalI), getscrollViewlData(finalI)));
        }

        Spinner dateSpinner = findViewById(R.id.date);
        dateSpinner.setAdapter(adapter);

        // スピナーのアイテム選択リスナーを設定
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemString = (String) parent.getItemAtPosition(position);
                Runnable mapLoader = dateMap.get(selectedItemString);
                if (mapLoader != null) {
                    mapLoader.run();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    //ルート表示を押して最初に表示されるルート(3日間の家庭訪問における1日目)の設定
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Google mapの定義
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        //家庭訪問1日目のスケジュール順に緯度経度の情報をString型の変数に格納
        //35.1711355,136.88552149999998/35.1696089,136.884084/35.1732838,136.88832890000003/...
        String locationDataString = createlocationData(0);
        //家庭訪問1日目のスケジュール順に住所や出席番号、家庭訪問の開始時間の情報をString型の変数に格納
        //開始地点/出席番号2番:鈴木次郎<〒453-0015 愛知県名古屋市中村区椿町６−９ 地下１階～５階> 06月04日12時00分/...
        String scrollViewlDataString = getscrollViewlData(0);
        loadMapAndNames(locationDataString, scrollViewlDataString);
    }

    //家庭訪問の〇日目が何月何日かを返すメソッド
    private String getSharedPreferencesData(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("visitingDate", MODE_PRIVATE);
        // SetUpで設定した8桁の数字表記の家庭訪問日を日付表記に変更
        String dayString;
        if (i == 0) {
            dayString = sharedPreferences.getString("day1", null);
        } else if (i == 1) {
            dayString = sharedPreferences.getString("day2", null);
        } else {
            dayString = sharedPreferences.getString("day3", null);
        }
        return dayString;
    }

    //家庭訪問の日付を8桁の整数表記から○月〇日(20240707→07月07日)表記に変更する
    private String formatDate(String date) {
        if (date == null || date.length() != 8) {
            return "";
        }
        String monthString = date.substring(4, 6);
        String dayString = date.substring(6, 8);
        return monthString + "月" + dayString + "日";
    }

    //家庭訪問1日目のスケジュール順に緯度経度の情報をString型の変数に格納
    private String createlocationData(int i) {
        //家庭訪問の開始地点の緯度経度
        String startPointLatLngString = getIntent().getStringExtra("startPointLatLngString");
        List<MyDataClass> myDataList = getMyDataList();
        //家庭訪問の緯度経度情報をまとめる変数
        StringBuilder latlngString = new StringBuilder();
        for (int y = -1; y < myDataList.size(); y++) {
            if (y < 0) {
                //家庭訪問の開始地点を追加
                latlngString.append(startPointLatLngString);
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i))) {
                if (latlngString.length() > 0) {
                    //区切りのスラッシュ
                    latlngString.append("/");
                }
                //mydataListから取り出した家庭訪問の各家庭の住所の緯度経度を追加
                //この時点ではlongitude latitudeのような不要な文字があるのでformatLatLngメソッドで緯度経度だけのデータにする
                latlngString.append(formatLatLng(myDataList.get(y)));
            }
        }
        //各家庭の緯度経度をまとめたものを返す
        return latlngString.toString();
    }

    //緯度と経度は(35.1711355,136.88552149999998)のように()の中に入っているのでそこだけを取り出す
    private String formatLatLng(MyDataClass myData) {
        String latlngString = myData.getLatLngString();
        int startIndex = latlngString.indexOf("(") + 1;
        int endIndex = latlngString.indexOf(")");
        return latlngString.substring(startIndex, endIndex);
    }

    //ScrollViewにて表示するdataの作成メソッド
    private String getscrollViewlData(int i) {
        List<MyDataClass> myDataList = getMyDataList();
        String homeVisitDataString = "";
        for (int y = -1; y < myDataList.size(); y++) {
            if (y < 0) {
                //家庭訪問の開始地点
                homeVisitDataString += "開始地点/";
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i)) && y + 1 < myDataList.size()) {
                //出席番号:生徒の名前 <住所> 家庭訪問の開始時間+/
                homeVisitDataString += "出席番号" + String.valueOf(myDataList.get(y).getStudentNumber()) + "番:" + myDataList.get(y).getChildName() + "<" + myDataList.get(y).getAddress().get(0) + "> " + formatSchedule(String.valueOf(myDataList.get(y).getSchedule())) + "/";
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i))) {
                homeVisitDataString += "出席番号" + String.valueOf(myDataList.get(y).getStudentNumber()) + "番:" + myDataList.get(y).getChildName() + " <" + myDataList.get(y).getAddress().get(0) + "> " + formatSchedule(String.valueOf(myDataList.get(y).getSchedule()));
            }
        }
        return homeVisitDataString;
    }

    //家庭訪問のscheduleを7桁の整数から○月〇日●時〇分(6041200→06月12日05時20分)に変換
    private String formatSchedule(String schedule) {
        Log.d("Maps", "schedule: " + schedule);
        if (schedule.length() != 7) {
            schedule = "0" + schedule;
            String monthString = schedule.substring(0, 2);
            String dayString = schedule.substring(2, 4);
            String hourString = "0" + schedule.substring(4, 5);
            String minuteString = schedule.substring(5, 7);
            return monthString + "月" + dayString + "日" + hourString + "時" + minuteString + "分";
        } else {
            String monthString = "0" + schedule.substring(0, 1);
            String dayString = schedule.substring(1, 3);
            String hourString = schedule.substring(3, 5);
            String minuteString = schedule.substring(5, 7);
            return monthString + "月" + dayString + "日" + hourString + "時" + minuteString + "分";

        }
    }

    // 共有プリファレンスからMyDataListを取得するメソッド
    private List<MyDataClass> getMyDataList() {
        // 共有プリファレンスのインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataList", MODE_PRIVATE);

        // 共有プリファレンスからJSON形式のデータを取得
        String jsonString = sharedPreferences.getString("myDataList", "");

        // JSON形式のデータをMyDataListに変換
        Gson gson = new Gson();
        Type type = new TypeToken<List<MyDataClass>>() {
        }.getType();
        List<MyDataClass> myDataList = gson.fromJson(jsonString, type);

        return myDataList;
    }


    //mapやgetscrollViewlに値を渡すハブの役割のメソッド
    private void loadMapAndNames(String locationData, String nameData) {
        try {
            //mapに関するすべてのデータをリセット
            latLngList.clear();
            nameList.clear();
            colorList.clear();
            locationsName.removeAllViews();
            mMap.clear();

            // locationDataをスラッシュで分割して緯度経度リストを取得
            String[] locArrayString = locationData.split("/");
            // nameDataをスラッシュで分割して名前リストを取得
            String[] nameArrayString = nameData.split("/");

            for (int i = 0; i < locArrayString.length; i++) {
                // 緯度経度をカンマで分割してLatLngオブジェクトを作成
                String[] latLngString = locArrayString[i].split(",");
                if (latLngString.length == 2) {
                    double latitudeDouble = Double.parseDouble(latLngString[0]);
                    double longitudeDouble = Double.parseDouble(latLngString[1]);
                    LatLng position = new LatLng(latitudeDouble, longitudeDouble);
                    latLngList.add(position);

                    // 名前リストから対応する名前を取得
                    String nameString = nameArrayString.length > i ? nameArrayString[i] : "Unknown";
                    nameList.add(nameString);

                    // 色リストから次の色を取得
                    int colorInt = getNextColor();
                    colorList.add(colorInt);

                    // 地図にピンを追加
                    addPinToMap(nameString, position, colorInt);
                    // スクロールビューに場所を追加
                    addLocationToScrollView(nameString, colorInt);
                }
            }

            // 最初の位置にカメラを移動し、ルートを描画
            if (!latLngList.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 17));
                drawRoute();  // ルートを描画するメソッドを呼び出す
            }
        } catch (Exception e) {
            Log.e("Maps", "エラーが発生しました。原因は以下", e);
        }
    }

    // ルートを描画するメソッド
    // Google マップの Directions API を使用してルート情報を取得し、ポリラインで描画
    private void drawRoute() {
        new Thread(() -> {
            try {
                StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
                urlBuilder.append("origin=").append(latLngList.get(0).latitude).append(",").append(latLngList.get(0).longitude);
                urlBuilder.append("&destination=").append(latLngList.get(latLngList.size() - 1).latitude).append(",").append(latLngList.get(latLngList.size() - 1).longitude);
                if (latLngList.size() > 2) {
                    urlBuilder.append("&waypoints=");
                    for (int i = 1; i < latLngList.size() - 1; i++) {
                        urlBuilder.append("via:").append(latLngList.get(i).latitude).append(",").append(latLngList.get(i).longitude);
                        if (i < latLngList.size() - 2) {
                            urlBuilder.append("|");
                        }
                    }
                }
                urlBuilder.append("&mode=driving");
                //APiキーの設定
                urlBuilder.append("&key=").append("AIzaSyBQ1Ak-I2NL5TP4K59ZI0VgzKk6HNZuusw");

                String urlString = urlBuilder.toString();
                Log.d("Maps", "Directions API URL: " + urlString);

                // Directions APIにリクエストを送信してレスポンスを取得
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder jsonResults = new StringBuilder();
                String lineString;
                while ((lineString = br.readLine()) != null) {
                    jsonResults.append(lineString);
                }
                br.close();

                Log.d("Maps", "API response: " + jsonResults.toString());
                // レスポンスからルート情報を取得してポリラインで描画
                JsonObject jsonObject = new Gson().fromJson(jsonResults.toString(), JsonObject.class);
                JsonArray routes = jsonObject.getAsJsonArray("routes");
                if (routes.size() > 0) {
                    JsonObject route = routes.get(0).getAsJsonObject();
                    JsonObject polyline = route.getAsJsonObject("overview_polyline");
                    String encodedString = polyline.get("points").getAsString();
                    List<LatLng> points = decodePoly(encodedString);

                    Log.d("Maps", "Polyline points: " + points);

                    runOnUiThread(() -> mMap.addPolyline(new PolylineOptions().addAll(points).width(5).color(Color.BLUE)));
                } else {
                    // ルートが見つからなかった場合のエラーメッセージを表示
                    JsonPrimitive errorMessage = jsonObject.getAsJsonPrimitive("error_message");
                    if (errorMessage != null) {
                        Log.e("Maps", "エラーが発生しました。原因は以下: " + errorMessage.getAsString());
                    } else {
                        Log.e("Maps", "原因不明のエラー");
                    }
                }
            } catch (Exception e) {
                Log.e("Maps", "ルートの描画に失敗しました", e);
            }
        }).start();
    }

    // エンコードされた文字列から座標情報をデコードするためのメソッド
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>(); // 座標情報を格納するリスト
        int index = 0, len = encoded.length();// 文字列のインデックスと長さ
        int latInt = 0, lng = 0; // 緯度と経度の初期値

        // 文字列の長さに達するまで繰り返す
        while (index < len) {
            int b, shift = 0, result = 0;
            // バイト値を取得し、ビットシフトとOR演算を行って座標値を復元する
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            latInt += dlat;

            shift = 0;
            result = 0;
            // バイト値を取得し、ビットシフトとOR演算を行って座標値を復元する
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            // LatLngオブジェクトを作成してリストに追加する
            LatLng p = new LatLng((((double) latInt / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        // 座標情報のリストを返す
        return poly;
    }

    // Colorクラスを使用して、与えられた色から色相値を計算するメソッド
    private float getHueFromColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);//整数の色値をHSV（色相、彩度、明度）の形式に変換し、色相値を取得します。
        return hsv[0];
    }

    //mapにピンを追加するメソッド
    private void addPinToMap(String locationName, LatLng position, int color) {
        // マーカーオプションを作成し、位置、タイトル、色を設定する
        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(locationName).icon(BitmapDescriptorFactory.defaultMarker(getHueFromColor(color))));
        if (marker != null) {
            marker.setTag(locationName);
        }
    }

    // スクロールビューに位置情報を追加するメソッド
    // スクロールビューに位置情報を追加するメソッド
// スクロールビューに位置情報を追加するメソッド
    private void addLocationToScrollView(String locationName, int color) {
        runOnUiThread(() -> {
            try {
                // テキストビューの作成
                TextView textView = new TextView(this);
                textView.setText(locationName);
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.setTextColor(Color.BLACK); // 文字色を黒に設定
                textView.setBackgroundColor(Color.WHITE); // 背景色を白に設定

                // 円を作成
                ShapeDrawable circle = new ShapeDrawable(new OvalShape());
                circle.setIntrinsicWidth(30);
                circle.setIntrinsicHeight(30);
                circle.getPaint().setColor(color);

                // 左側に円を表示するためにDrawableを設定
                textView.setCompoundDrawablesWithIntrinsicBounds(circle, null, null, null);
                textView.setCompoundDrawablePadding(16);

                // テキストビューにクリックリスナーを追加
                textView.setOnClickListener(v -> {
                    for (int j = 0; j < nameList.size(); j++) {
                        if (nameList.get(j).equals(locationName)) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(j), 17));
                            break;
                        }
                    }
                });

                // ボーダーラインの作成
                View border = new View(this);
                border.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // 横幅は親と同じ
                        2 // 高さは2dp
                ));
                border.setBackgroundColor(Color.GRAY); // ボーダーラインの色を設定

                // レイアウトの作成
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(textView);
                layout.addView(border);

                // スクロールビューにレイアウトを追加
                locationsName.addView(layout);
            } catch (Exception e) {
                Log.e("Maps", "エラーが発生しました。原因は以下", e);
            }
        });
    }


    private int getNextColor() {
        int color = COLORS[colorIndex];
        colorIndex = (colorIndex + 1) % COLORS.length;
        return color;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BackMain) {
            finish();
        }
    }


    @Override
    //マーカーがクリックされた際の処理を行うメソッド
    public boolean onMarkerClick(Marker marker) {
        // マーカーから場所の名前を取得します。
        String locationName = (String) marker.getTag();
        // 場所の名前がnullではない場合に処理を実行します。
        if (locationName != null) {
            // マーカーのタイトルとして場所の名前を設定します。
            marker.setTitle(locationName);
            // マーカーの情報ウィンドウを表示します。
            marker.showInfoWindow();
        }
        // デフォルトの動作も実行するためにfalseを返します。
        return false;
    }
}
