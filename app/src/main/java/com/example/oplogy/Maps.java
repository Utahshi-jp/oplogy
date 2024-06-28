package com.example.oplogy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.example.oplogy.databinding.MapsBinding;

public class Maps extends FragmentActivity implements View.OnClickListener {

    private WebView webView;
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
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        loadMapInWebView("35.09050879999539,136.87845379325216/35.09284820618655,136.88165119390393/35.09364708442631,136.88171563326418");
    }

    private void loadMapInWebView(String locations) {
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

        webView.loadUrl(urlBuilder.toString());
    }

    @Override
    public void onClick(View view) {
        if (view == backMain) {
            Intent backMain = new Intent(Maps.this, MainActivity.class);
            startActivity(backMain);
        }
    }
}
