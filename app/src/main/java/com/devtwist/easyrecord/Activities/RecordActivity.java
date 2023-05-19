package com.devtwist.easyrecord.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.devtwist.easyrecord.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RecordActivity extends AppCompatActivity {

    private ImageView _createRecord, _searchRecord, _viewProfile;
    private Intent intent;
    private AdView _recordAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        _createRecord = findViewById(R.id._createRecord);
        _searchRecord = findViewById(R.id._searchRecord);
        _viewProfile = findViewById(R.id._viewProfile);
        _recordAdView = findViewById(R.id._recordAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        _recordAdView.loadAd(adRequest);

        _createRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RecordActivity.this, CreateRecordActivity.class);
                startActivity(intent);
            }
        });

        _searchRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RecordActivity.this, SearchRecordActivity.class);
                startActivity(intent);
            }
        });

        _viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, UserProfileActivity.class));
            }
        });

    }
}