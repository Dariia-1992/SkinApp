package com.mikivstudio.appnamehere;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.postDelayed(mPendingLauncherRunnable, 1000);
    }

    private final Runnable mPendingLauncherRunnable = () -> {
        Intent mm = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mm);
        SplashActivity.this.finish();
    };
}