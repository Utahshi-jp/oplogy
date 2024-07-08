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
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.oplogy.databinding.MapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;
    private LinearLayout locationsName;

    Random random = new Random();

    static int[] colors = new int[]{
            Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW
    };

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

        locationsName = findViewById(R.id.locationsName); // locationsNameの初期化
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String locationData = "35.09050879999539,136.87845379325216/35.091950716938875,136.8826598363985/35.09273643623442,136.88154941341296";
        String nameData = "名古屋港水族館/2番目/3番目";

        loadMapAndNames(locationData, nameData);
    }

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

                    int color = colors[random.nextInt(colors.length)];
                    colorList.add(color);

                    addPinToMap(name, position, color);
                    addLocationToScrollView(name, color);
                }
            }

            if (!latLngList.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 17));
                mMap.addPolyline(new PolylineOptions().addAll(latLngList).width(5).color(Color.BLUE));
            }
        } catch (Exception e) {
            Log.e("Maps", "Error loading maps and names", e);
        }
    }

    private float getHueFromColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }

    private void addPinToMap(String locationName, LatLng position, int color) {
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(locationName)
                .icon(BitmapDescriptorFactory.defaultMarker(getHueFromColor(color)))
        );
    }

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
                Log.e("Maps", "Error adding to ScrollView", e);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.BackMain) {
            finish();
        }
    }
}
