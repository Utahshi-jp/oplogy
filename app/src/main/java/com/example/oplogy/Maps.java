package com.example.oplogy;

import androidx.fragment.app.FragmentActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.oplogy.databinding.MapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maps extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;
    private LinearLayout locationsName;

    private static final int[] COLORS = new int[]{
            Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW
    };
    private int colorIndex = 0;

    private List<LatLng> latLngList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Integer> colorList = new ArrayList<>();

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
    }


    // 書くところはここのみです。後は触らなくて大丈夫です。
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // ここに緯度と経度を入れてください「/」で区切って取得する処理となっています
        String locationData = "35.09050879999539,136.87845379325216/35.091950716938875,136.8826598363985/35.09273643623442,136.88154941341296/35.09473643623442,136.88154941341296/35.09673643623442,136.88154941341296";
        // ここに名前等いれたい情報を乗せてください。
        String nameData = "名古屋港水族館/2番目/3番目/4番目/5番目";

        loadMapAndNames(locationData, nameData);
    }


    // 名前入力の処理
    private void loadMapAndNames(String locationData, String nameData) {
        try {
            String[] locArray = locationData.split("/");
            String[] nameArray = nameData.split("/");

            for (int i = 0; i < locArray.length; i++) {
                String[] latLng = locArray[i].split(",");
                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);
                    LatLng position = new LatLng(latitude, longitude);
                    latLngList.add(position);

                    String name = nameArray.length > i ? nameArray[i] : "Unknown";
                    nameList.add(name);

                    int color = getNextColor();
                    colorList.add(color);

                    addPinToMap(name, position, color);
                    addLocationToScrollView(name, color);
                }
            }

            if (!latLngList.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 17));
                drawRoute();
            }
        } catch (Exception e) {
            Log.e("Maps", "Error loading maps and names", e);
        }
    }

    //ルート表示の処理（APIがちゃんと作動している時に動く所です）
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

                URL url = new URL(urlBuilder.toString());
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

                Log.d("Maps", "API response: " + jsonResults.toString()); // APIレスポンス全体をログに記録

                JsonObject jsonObject = new Gson().fromJson(jsonResults.toString(), JsonObject.class);
                JsonArray routes = jsonObject.getAsJsonArray("routes");
                if (routes.size() > 0) {
                    JsonObject route = routes.get(0).getAsJsonObject();
                    JsonObject polyline = route.getAsJsonObject("overview_polyline");
                    String encodedString = polyline.get("points").getAsString();
                    List<LatLng> points = decodePoly(encodedString);

                    Log.d("Maps", "Polyline points: " + points);

                    runOnUiThread(() -> {
                        mMap.addPolyline(new PolylineOptions().addAll(points).width(5).color(Color.BLUE));
                    });
                } else {
                    // エラーが発生した場合の処理
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


    // マップのピン等に入れる色の処理
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


    // ScrollViewのTextViewの中の処理（名前や、色等）
    private void addLocationToScrollView(String locationName, int color) {
        runOnUiThread(() -> {
            try {
                TextView textView = new TextView(this);
                textView.setText(locationName);
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.setBackgroundColor(color);

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

    //色の重複（赤・赤等を避ける）処理
    private int getNextColor() {
        int color = COLORS[colorIndex];
        colorIndex = (colorIndex + 1) % COLORS.length;
        return color;
    }

    //クリックしたらメイン画面に戻ります
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.BackMain) {
            finish();
        }
    }


    //マップのピンをクリックしたときの処理
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
