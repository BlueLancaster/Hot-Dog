package com.nukcsie.nothotdog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.nukcsie.nothotdog.models.RecognitionItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();   // 隱藏導覽業
        setContentView(R.layout.activity_results);  // 設置畫面

        ArrayList<RecognitionItem> recognitionItems =   // 取出分析結果的資料
                getIntent().getParcelableArrayListExtra("results");
        ImageView imageView = findViewById(R.id.resultImage);
        TextView result = findViewById(R.id.resultLabel);
        TextView label1 = findViewById(R.id.label1);
        TextView label2 = findViewById(R.id.label2);
        TextView label3 = findViewById(R.id.label3);

        Collections.sort(recognitionItems);
        Collections.reverse(recognitionItems);

        if (recognitionItems.get(0).label.equals("Hot Dog"))
            imageView.setImageResource(R.drawable.hot_dog);
        else if (recognitionItems.get(0).label.equals("Not Hot Dog"))
            imageView.setImageResource(R.drawable.pizza);
        else
            imageView.setImageResource(R.drawable.dog);

        result.setText(recognitionItems.get(0).label);
        label1.setText(recognitionItems.get(0).label + ": "
                + recognitionItems.get(0).getConfidence() + "%");
        label2.setText(recognitionItems.get(1).label + ": "
                + recognitionItems.get(1).getConfidence() + "%");
        label3.setText(recognitionItems.get(2).label + ": "
                +recognitionItems.get(2).getConfidence() + "%");

        CardView shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> share(recognitionItems.get(0).label));
    }



    private void share(String item) {   // 分享功能實作
        Intent sendIntent = new Intent(Intent.ACTION_SEND); // 建立intent來實作分享功能
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Share");   // 設定分享訊息的標題
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I found a " + item);    // 設定分享訊息的內容
        sendIntent.setType("text/plain");   // 設定分享訊息的類型
        startActivity(Intent.createChooser(sendIntent, null));  // 執行分享功能的intent
    }
}