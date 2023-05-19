package com.devtwist.easyrecord.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;

import com.devtwist.easyrecord.R;


public class MainActivity extends AppCompatActivity {

    private Intent intent;
    private boolean isLogedin, isProfileCreated;
    private SharedPreferences preferences;
    private LinearLayout _logoLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _logoLayout = findViewById(R.id._logoLayout);

        _logoLayout.setTranslationX(0f);
        _logoLayout.setTranslationY(0f);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _logoLayout.animate().translationXBy(1f).translationYBy(1f).setDuration(2000);
            }

            @Override
            public void onFinish() {
                preferences = getSharedPreferences("MyData", MODE_PRIVATE);
                if (preferences.contains("isLogedIn") && preferences.contains("isProfileCreated")) {
                    isLogedin = preferences.getBoolean("isLogedIn", false);
                    isProfileCreated = preferences.getBoolean("isProfileCreated", false);
                }
                if (isLogedin && isProfileCreated) {
                    intent = new Intent(MainActivity.this, RecordActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (!isLogedin) {
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        intent = new Intent(MainActivity.this, CreateProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }.start();



    }
}