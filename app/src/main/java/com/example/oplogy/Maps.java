package com.example.oplogy;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.oplogy.databinding.MapsBinding;

import java.util.Locale;
import java.util.Map;

public class Maps extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener{

    //        ボタンの戻る処理
    ImageView backMain;
    private GoogleMap mMap;
    private MapsBinding binding;

    private LatLng location;

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

        // ↓ここに地点の処理を書いておく↓

        location = new LatLng(35.09050879999539, 136.87845379325216);
        mMap.addMarker(new MarkerOptions().position(location).title("名古屋港水族館"));

        /// 地図の倍率を指定
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(tapLocation -> {
            // tapされた位置の緯度経度
            location = new LatLng(tapLocation.latitude, tapLocation.longitude);
            String str = String.format(Locale.JAPAN, "%f, %f", tapLocation.latitude, tapLocation.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

//            ピンの処理
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(str)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .anchor(0.5f, 0.5f)
            );


        });
    }

    @Override
    public void onClick(View view) {
        if(view == backMain){
            Intent backMain = new Intent(Maps.this,MainActivity.class);
            startActivity(backMain);
        }
    }
}