package com.example.oplogy;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.oplogy.databinding.MapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class Maps extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private static final String TAG = "MapsActivity";
    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng startPoint = new LatLng(35.09050879999539, 136.87845379325216);
        LatLng endPoint = new LatLng(35.09364708442631, 136.88171563326418);

        addNumberedMarker(startPoint, "1: 名古屋港水族館");
        addNumberedMarker(new LatLng(35.09284820618655, 136.88165119390393), "2: Waypoint");
        addNumberedMarker(endPoint, "3: Destination");

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 17));

        // Directions API リクエスト
        new Thread(() -> {
            try {
                DirectionsResult result = DirectionsApi.newRequest(new GeoApiContext.Builder()
                                .apiKey("AIzaSyBQ1Ak-I2NL5TP4K59ZI0VgzKk6HNZuusw") // DirectionsAPIキーの取得
                                .build())
                        .mode(TravelMode.DRIVING)
                        .origin(new com.google.maps.model.LatLng(startPoint.latitude, startPoint.longitude))
                        .destination(new com.google.maps.model.LatLng(endPoint.latitude, endPoint.longitude))
                        .await();

                if (result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];

                    List<LatLng> decodedPath = new ArrayList<>();
                    for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                        decodedPath.add(new LatLng(point.lat, point.lng));
                    }

                    runOnUiThread(() -> {
                        PolylineOptions polylineOptions = new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10);
                        mMap.addPolyline(polylineOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                LatLngBounds.builder().include(startPoint).include(endPoint).build(), 100));
                    });
                } else {
                    Log.e(TAG, "No routes found");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error fetching directions", ex);
            }
        }).start();
    }

//    ここで入れている地点に、番号を振っている
    private void addNumberedMarker(LatLng position, String title) {
        mMap.addMarker(new MarkerOptions().position(position).title(title));
    }


//    クリックするとメイン画面に戻る処理
    @Override
    public void onClick(View view) {
        if (view == backMain) {
            Intent backMain = new Intent(Maps.this, MainActivity.class);
            startActivity(backMain);
        }
    }


//    LocationListenerの呼び出し時に実装するもの
    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
