package com.example.oplogy;

import android.content.SharedPreferences;
import android.graphics.Color;
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

    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;
    private LinearLayout locationsName;
    private Spinner dateSpinner;
    //GoogleMapAPiで使用可能な色
    private static final int[] COLORS = new int[]{
            Color.parseColor("#007FFF"), // HUE_AZURE
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
    private int colorIndex = 0;
    private List<LatLng> latLngList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Integer> colorList = new ArrayList<>();

    private Map<String, Runnable> dateMap = new HashMap<>();

    private AppDatabase db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backMain = findViewById(R.id.BackMain);
        backMain.setOnClickListener(this);

        locationsName = findViewById(R.id.locationsName);

        String dateData = formatDate(getSharedPreferencesData(0)) + "/" + formatDate(getSharedPreferencesData(1)) + "/" + formatDate(getSharedPreferencesData(2));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] dates = dateData.split("/");
        for (String date : dates) {
            adapter.add(date);
        }

        // 各日付に対応するRunnableを設定する
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            String dayString = getSharedPreferencesData(i);
            String formattedDayString = formatDate(dayString);
            if (i == 0) {
                dateMap.put(formattedDayString, () -> firstMapAndNames(createlocationData(finalI), getData(finalI)));
            } else if (i == 1) {
                dateMap.put(formattedDayString, () -> secondMapAndNames(createlocationData(finalI), getData(finalI)));
            } else {
                dateMap.put(formattedDayString, () -> thirdMapAndNames(createlocationData(finalI), getData(finalI)));
            }
        }
        dateSpinner = findViewById(R.id.date);
        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Runnable mapLoader = dateMap.get(selectedItem);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        String latlngString = createlocationData(0);
        String homeVisitDataString = getData(0);
        firstMapAndNames(latlngString, homeVisitDataString);
    }

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


    private String formatDate(String date) {
        if (date == null || date.length() != 8) {
            return "";
        }
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "月" + day + "日";
    }

    private String createlocationData(int i) {
        String startPointLatLngString = getIntent().getStringExtra("startPointLatLngString");
        List<MyDataClass> myDataList = getMyDataList();
        StringBuilder latlngString = new StringBuilder();

        for (int y = -1; y < myDataList.size(); y++) {
            if (y < 0) {
                latlngString.append(startPointLatLngString);
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i))) {
                if (latlngString.length() > 0) {
                    latlngString.append("/");
                }
                latlngString.append(formatLatLng(myDataList.get(y)));
            }
        }

        return latlngString.toString();
    }

    private String formatLatLng(MyDataClass myData) {
        String latlng = myData.getLatLngString();
        int startIndex = latlng.indexOf("(") + 1;
        int endIndex = latlng.indexOf(")");
        return latlng.substring(startIndex, endIndex);
    }

    private String getData(int i) {
        List<MyDataClass> myDataList = getMyDataList();
        String homeVisitDataString = "";
        for (int y = -1; y < myDataList.size(); y++) {
            if (y < 0) {
                homeVisitDataString += "開始地点/";
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i)) && y + 1 < myDataList.size()) {
                homeVisitDataString += "出席番号" + String.valueOf(myDataList.get(y).getStudentNumber()) + "番:" + myDataList.get(y).getChildName() + "<" + myDataList.get(y).getAddress().get(0) + "> " + formatSchedule(String.valueOf(myDataList.get(y).getSchedule())) + "/";
            } else if (myDataList.get(y).getScheduleDay().equals(getSharedPreferencesData(i))) {
                homeVisitDataString += "出席番号" + String.valueOf(myDataList.get(y).getStudentNumber()) + "番:" + myDataList.get(y).getChildName() + " <" + myDataList.get(y).getAddress().get(0) + "> " + formatSchedule(String.valueOf(myDataList.get(y).getSchedule()));
            }
        }
        return homeVisitDataString;
    }

    private String formatSchedule(String schedule) {
        String month = "0" + schedule.substring(0, 1);
        String day = schedule.substring(1, 3);
        String hour = schedule.substring(3, 5);
        String minute = schedule.substring(5, 7);
        return month + "月" + day + "日" + hour + "時" + minute + "分";
    }

    // 共有プリファレンスからMyDataListを取得するメソッド
    private List<MyDataClass> getMyDataList() {
        // 共有プリファレンスのインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataList", MODE_PRIVATE);

        // 共有プリファレンスからJSON形式のデータを取得
        String json = sharedPreferences.getString("myDataList", "");

        // JSON形式のデータをMyDataListに変換
        Gson gson = new Gson();
        Type type = new TypeToken<List<MyDataClass>>() {
        }.getType();
        List<MyDataClass> myDataList = gson.fromJson(json, type);

        return myDataList;
    }

    private void firstMapAndNames(String latlngStringFirstDay, String homeVisitDataString) {
        String locationData = latlngStringFirstDay;
        String labelData = homeVisitDataString;
        Log.d("maps", "locationData" + locationData);
        Log.d("maps", "labelData" + labelData);
        loadMapAndNames(locationData, labelData);
    }

    private void secondMapAndNames(String latlngStringSeconfDay, String homeVisitDataString) {
        String locationData = latlngStringSeconfDay;
        String labelData = homeVisitDataString;
        loadMapAndNames(locationData, labelData);
    }

    private void thirdMapAndNames(String latlngStringThirdDay, String homeVisitDataString) {
        String locationData = latlngStringThirdDay;
        String labelData = homeVisitDataString;
        loadMapAndNames(locationData, labelData);
    }


    private void loadMapAndNames(String locationData, String nameData) {
        try {
            latLngList.clear();
            nameList.clear();
            colorList.clear();
            locationsName.removeAllViews();
            mMap.clear();

            // locationDataをスラッシュで分割して緯度経度リストを取得
            String[] locArray = locationData.split("/");
            // nameDataをスラッシュで分割して名前リストを取得
            String[] nameArray = nameData.split("/");

            for (int i = 0; i < locArray.length; i++) {
                // 緯度経度をカンマで分割してLatLngオブジェクトを作成
                String[] latLng = locArray[i].split(",");
                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);
                    LatLng position = new LatLng(latitude, longitude);
                    latLngList.add(position);

                    // 名前リストから対応する名前を取得
                    String name = nameArray.length > i ? nameArray[i] : "Unknown";
                    nameList.add(name);

                    // 色リストから次の色を取得
                    int color = getNextColor();
                    colorList.add(color);

                    // 地図にピンを追加
                    addPinToMap(name, position, color);
                    // スクロールビューに場所を追加
                    addLocationToScrollView(name, color);
                }
            }

            // 最初の位置にカメラを移動し、ルートを描画
            if (!latLngList.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 17));
                drawRoute();  // ルートを描画するメソッドを呼び出す
            }
        } catch (Exception e) {
            Log.e("Maps", "Error loading maps and names", e);
        }
    }

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
                urlBuilder.append("&key=").append(getString(R.string.maps_api_key));

                String urlString = urlBuilder.toString();
                Log.d("Maps", "Directions API URL: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder jsonResults = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonResults.append(line);
                }
                br.close();

                Log.d("Maps", "API response: " + jsonResults.toString());

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
                    JsonPrimitive errorMessage = jsonObject.getAsJsonPrimitive("error_message");
                    if (errorMessage != null) {
                        Log.e("Maps", "Error: " + errorMessage.getAsString());
                    } else {
                        Log.e("Maps", "No routes found and no error message provided");
                    }
                }
            } catch (Exception e) {
                Log.e("Maps", "Error drawing route", e);
            }
        }).start();
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    private float getHueFromColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }

    private void addPinToMap(String locationName, LatLng position, int color) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(locationName)
                .icon(BitmapDescriptorFactory.defaultMarker(getHueFromColor(color)))
        );
        if (marker != null) {
            marker.setTag(locationName);
        }
    }

    private void addLocationToScrollView(String locationName, int color) {
        runOnUiThread(() -> {
            try {
                TextView textView = new TextView(this);
                textView.setText(locationName);
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.setBackgroundColor(color);

                // テキストビューにクリックリスナーを追加
                textView.setOnClickListener(v -> {
                    // 名前リストからインデックスを取得
                    int index = nameList.indexOf(locationName);
                    if (index != -1) {
                        LatLng position = latLngList.get(index);
                        // カメラを該当ピンの位置に移動
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
                    }
                });

                View underline = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3
                );
                params.setMargins(0, 0, 0, 16);
                underline.setLayoutParams(params);
                underline.setBackgroundColor(Color.BLACK);

                locationsName.addView(textView);
                locationsName.addView(underline);
            } catch (Exception e) {
                Log.e("Maps", "Error adding location to scroll view", e);
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
    public boolean onMarkerClick(Marker marker) {
        String locationName = (String) marker.getTag();
        if (locationName != null) {
            marker.setTitle(locationName);
            marker.showInfoWindow();
        }
        return false;
    }
}
