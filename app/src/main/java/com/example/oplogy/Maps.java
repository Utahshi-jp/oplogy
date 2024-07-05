package com.example.oplogy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.example.oplogy.databinding.MapsBinding;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Maps extends FragmentActivity implements View.OnClickListener {

    private WebView webView;
    ImageView backMain;
    private MapsBinding binding;
    private ArrayList<Parcelable> myDataList;;
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
        List<MyDataClass> myDataList=getMyDataList();
        String latlngString = "";
        for (int i = 0; i < myDataList.size(); i++) {
            String latlng = myDataList.get(i).getLatLngString();
            int startIndex = latlng.indexOf("(") + 1;
            int endIndex = latlng.indexOf(")");
            String latlngOnly = latlng.substring(startIndex, endIndex);
            latlngString += latlngOnly;
            if (i < myDataList.size() - 1) {
                latlngString += "/";
            }
        }
        Log.d("Maps","latlngString"+latlngString);
        loadMapInWebView(latlngString);
    }

    // 共有プリファレンスからMyDataListを取得するメソッド
    private List<MyDataClass> getMyDataList() {
        // 共有プリファレンスのインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataList", MODE_PRIVATE);

        // 共有プリファレンスからJSON形式のデータを取得
        String json = sharedPreferences.getString("myDataList", "");

        // JSON形式のデータをMyDataListに変換
        Gson gson = new Gson();
        Type type = new TypeToken<List<MyDataClass>>() {}.getType();
        List<MyDataClass> myDataList = gson.fromJson(json, type);

        return myDataList;
    }

//    WebViewの処理です（Mapの中の処理をやっています）
    private void loadMapInWebView(String locations) {
//        区切ることで、追加の地点を入れて、最終地点にピンを打ってある状態です
        String[] locArray = locations.split("/");
//        ↓URLで経路案内（車）での表示をしています
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
