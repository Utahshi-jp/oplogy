package com.example.oplogy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class Map_Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    ImageView toMain;
    private  GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);


//        ボタンの戻る処理
        toMain = findViewById(R.id.toMain);
        toMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == toMain){
            Intent toMain = new Intent(Map_Activity.this,MainActivity.class);
            startActivity(toMain);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        FragmentManager fragmentManager = getSupportFragmentManager();

    }
}