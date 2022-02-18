package com.nukcsie.nothotdog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.nukcsie.nothotdog.models.RecognitionItem;
import com.nukcsie.nothotdog.utils.ImageAnalyzer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonRealTimeActivity extends AppCompatActivity {
    private Camera camera;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();   // 隱藏上方的導覽列
        setContentView(R.layout.activity_non_real_time);    // 設定畫面配置
        startCamera();  // 初始化並開啟相機
    }

    private Context getContext() {
        return this;
    }

    private void startCamera() {
        ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();   //  建立一個單執行緒化的執行緒
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =  // 非同步執行
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {    // 設定監聽
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();    // 設定鏡頭畫面

                @SuppressLint("RestrictedApi")
                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setTargetResolution(new Size(224, 224))
                        .setBufferFormat(ImageFormat.YUV_420_888)
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();   // 設定拍攝的畫面規格

                // 設置捕捉畫面按鈕和監聽器
                findViewById(R.id.captureButton).setOnClickListener(view -> imageCapture.takePicture(cameraExecutor,
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull @NotNull ImageProxy image) {  // 當捕捉成功畫面後呼叫
                                super.onCaptureSuccess(image);
                                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(getContext(),
                                        ImageAnalyzer.ANALYSIS_MODE.NON_REAL_TIME); // 設置新的圖片分析器
                                imageAnalyzer.analyze(image);   // 分析圖片
                                startResultsActivity(imageAnalyzer.getRecognitionOutputs());    // 啟動分析結果畫面
                            }
                        }));
                CameraSelector cameraSelector = new CameraSelector.Builder()       // 設定為後鏡頭拍攝
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                PreviewView previewView = findViewById(R.id.previewView);       // 設置相機畫面元件
                preview.setSurfaceProvider(previewView.getSurfaceProvider());   // 傳入相機畫面
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);   // 連接camera和其他元件生命週期
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        ImageView flashlight = findViewById(R.id.flashlight);   // 設置閃光燈切換按鈕
        flashlight.setOnClickListener(v -> {       // 設置閃光燈切換監聽器
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
            flashlight.setImageResource(isFlashOn ?
                    R.drawable.ic_baseline_flash_off_24 :
                    R.drawable.ic_outline_flash_on_24);
        });
    }

    private void startResultsActivity(ArrayList<RecognitionItem> recognitionItems) {    // 啟動分析結果畫面
        Intent intent = new Intent(this, ResultsActivity.class);    // 設置intent
        intent.putExtra("results", recognitionItems);   // 把分析結果放入intent儲存
        startActivity(intent);  // 執行intent
    }
}