package com.nukcsie.nothotdog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // 隱藏上方的導覽列
        setContentView(R.layout.activity_main); // 設定畫面配置

        if (!hasCameraPermission()) // 檢查是否有相機權限
            requestPermission();    // 獲取相機權限

        CardView nonRealTimeButton = findViewById(R.id.nonRealTImeButton);  // 建立物件獲取畫面上的子配件參考
        CardView realTimeButton = findViewById(R.id.realTimeButton);
        CardView shareButton = findViewById(R.id.shareButton);

        // 設立捕捉辨識模式的button的點擊監聽器
        nonRealTimeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NonRealTimeActivity.class);    // 建立intent並設定下個畫面(Activity)
            startActivity(intent);  // 啟動下一個畫面
        });

        // 設立立即辨識模式的button的點擊監聽器
        realTimeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RealTimeActivity.class);   // 建立intent並設定下個畫面(Activity)
            startActivity(intent);  // 啟動下一個畫面
        });

        shareButton.setOnClickListener(v -> share());   // 設立分享按鈕的監聽器
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this,  // 回傳相機權限
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,     // 向內部請求權限
                new String[]{Manifest.permission.CAMERA}, 4234);
    }

    private void share() {  // 分享功能實作
        Intent sendIntent = new Intent(Intent.ACTION_SEND); // 建立intent來實作分享功能
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Share");   // 設定分享訊息的標題
        sendIntent.putExtra(Intent.EXTRA_TEXT, "See Food - The Shazam for Food");   // 設定分享訊息的內容
        sendIntent.setType("text/plain");   // 設定分享訊息的類型
        startActivity(Intent.createChooser(sendIntent, null));  // 執行分享功能的intent
    }

}