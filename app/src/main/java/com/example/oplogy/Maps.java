package com.example.oplogy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.oplogy.databinding.MapsBinding;

import java.util.Random;

public class Maps extends FragmentActivity implements View.OnClickListener {

    private WebView webView;
    private LinearLayout locationsName;
    ImageView backMain;
    private MapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backMain = findViewById(R.id.BackMain);
        backMain.setOnClickListener(this);

        webView = findViewById(R.id.webView);
        locationsName = findViewById(R.id.locationsName);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // 初期の緯度経度と名前のセットをロード
        loadMapInWebView("35.09050879999539,136.87845379325216/35.091950716938875,136.8826598363985/35.09273643623442,136.88154941341296");
        loadName("名古屋港水族館/2番目/3番目");
    }

    // WebViewの処理です（Mapの中の処理をやっています）
    private void loadMapInWebView(String locations) {
        try {
            String[] locArray = locations.split("/");
            StringBuilder urlBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1&travelmode=driving");

            if (locArray.length > 0) {
                urlBuilder.append("&origin=").append(locArray[0]);

                if (locArray.length > 1) {
                    urlBuilder.append("&destination=").append(locArray[locArray.length - 1]);

                    if (locArray.length > 2) {
                        urlBuilder.append("&waypoints=");
                        for (int i = 1; i < locArray.length - 1; i++) {
                            urlBuilder.append(locArray[i]);
                            if (i < locArray.length - 2) {
                                urlBuilder.append("|");
                            }
                        }
                    }
                }
            }

            runOnUiThread(() -> webView.loadUrl(urlBuilder.toString()));
        } catch (Exception e) {
            Log.e("Maps", "Error loading map in WebView", e);
        }
    }

    // 新しい名前を追加するメソッドです
    private void loadName(String locNames) {
        try {
            String[] locArray = locNames.split("/");

            for (String loc : locArray) {
                runOnUiThread(() -> addLocationToScrollView(loc));
            }
        } catch (Exception e) {
            Log.e("Maps", "Error loading names", e);
        }
    }

    // ScrollViewに新しい場所の名前を追加するメソッドです
    private void addLocationToScrollView(String locationName) {
        runOnUiThread(() -> {
            try {
                TextView textView = new TextView(this);
                textView.setText(locationName);
                textView.setTextSize(16);
                textView.setPadding(16, 16, 16, 16);

                // ランダムな背景色を設定
                Random random = new Random();
                int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                textView.setBackgroundColor(color);

                // 下線のViewを追加
                View underline = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2 // 下線の高さをピクセル単位で設定
                );
                params.setMargins(0, 0, 0, 16); // 下線の上下のマージンを設定
                underline.setLayoutParams(params);
                underline.setBackgroundColor(Color.BLACK); // 下線の色を設定

                // TextViewと下線のViewを追加
                locationsName.addView(textView);
                locationsName.addView(underline);
            } catch (Exception e) {
                Log.e("Maps", "Error adding location to ScrollView", e);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == backMain) {
            Intent backMain = new Intent(Maps.this, MainActivity.class);
            startActivity(backMain);
        }
    }
}
