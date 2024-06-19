package com.example.oplogy;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;

import java.util.Locale;
import java.util.Map;

public class Maps extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener{

    //        ボタンの戻る処理
    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;

    private LatLng loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = MapsBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backMain = findViewById(R.id.BackMain);
        backMain.setOnClickListener(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /// 地図の倍率を指定

        // ↓ここに地点の処理を書いておく↓
        // Add a marker in Sydney and move the camera
        loc = new LatLng(35.09050879999539, 136.87845379325216);
        mMap.addMarker(new MarkerOptions().position(loc).title("名古屋港水族館"));
        /// 表示位置を地図に指定
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

        LatLng startLatLng = new LatLng(35.09050879999539, 136.87845379325216);
        LatLng secandLatLng = new LatLng(35.09284820618655, 136.88165119390393);
        LatLng thirdLatLng = new LatLng(35.09364708442631, 136.88171563326418);


        Marker startMaker = mMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .title("名古屋港水族館")
        );

        Marker secondMaker = mMap.addMarker(new MarkerOptions()
                .position(secandLatLng)
                .title("2番目")
        );

        Marker thirdMaker = mMap.addMarker(new MarkerOptions()
                .position(thirdLatLng)
                .title("3番目")
        );

        drowRoute(startLatLng,secandLatLng,thirdLatLng);
    }

    private  void drowRoute(LatLng startLatLng,LatLng secondLatLung,LatLng thirdLatLng){
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBQ1Ak-I2NL5TP4K59ZI0VgzKk6HNZuusw")
                .build();
    }

    @Override
    public void onClick(View view) {
        if(view == backMain){
            Intent backMain = new Intent(Maps.this,MainActivity.class);
            startActivity(backMain);
        }
    }
}