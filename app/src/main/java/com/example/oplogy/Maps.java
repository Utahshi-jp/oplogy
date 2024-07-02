package com.example.oplogy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.oplogy.databinding.MapsBinding;

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

        // ここにデータを入れておいてください、処理は[/]で区切っています(緯度と経度で入れているので、そこで一度試してください)
        loadMapInWebView("35.09050879999539,136.87845379325216,名古屋港水族館/35.091950716938875,136.8826598363985,2番目/35.09273643623442,136.88154941341296,3番目");
    }

    // WebViewの処理です（Mapの中の処理をやっています）
    private void loadMapInWebView(String locations) {
        // 区切ることで、追加の地点を入れて、最終地点にピンを打ってある状態です
        String[] locArray = locations.split("/");

        // 各地点の名前をScrollViewに表示
        for (String loc : locArray) {
            String[] parts = loc.split(",");
            if (parts.length == 3) {
                addLocationToScrollView(parts[2]);
            }
        }

        // URLで経路案内（車）での表示をしています
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

        webView.loadUrl(urlBuilder.toString());
    }

    private void addLocationToScrollView(String locationName) {
        TextView textView = new TextView(this);
        textView.setText(locationName);
        textView.setTextSize(16);
        textView.setPadding(16, 16, 16, 16);
        locationsName.addView(textView);
    }

    @Override
    public void onClick(View view) {
        if (view == backMain) {
            Intent backMain = new Intent(Maps.this, MainActivity.class);
            startActivity(backMain);
        }
    }
}
