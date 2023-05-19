package com.devtwist.easyrecord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.devtwist.easyrecord.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ViewItemActivity extends AppCompatActivity {

    private WebView _viewWebView;
    private Intent intent;
    private Bundle bundle;
    private InterstitialAd mInterstitialAd;
    private AdView _vRecordAdView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        setUpAds();
        runAds();

        _viewWebView = findViewById(R.id._viewWebView);
        _vRecordAdView = findViewById(R.id._vRecordAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        _vRecordAdView.loadAd(adRequest);

        intent = getIntent();
        bundle = intent.getExtras();

        if (bundle.getString("tag").equals("t")) {
            _viewWebView.getSettings().setJavaScriptEnabled(true);
            _viewWebView.loadUrl("https://pages.flycricket.io/easy-record/terms.html");
            _viewWebView.setVisibility(View.VISIBLE);
        } else if (bundle.getString("tag").equals("p")) {
            _viewWebView.getSettings().setJavaScriptEnabled(true);
            _viewWebView.loadUrl("https://pages.flycricket.io/easy-record/privacy.html");
            _viewWebView.setVisibility(View.VISIBLE);
        }

    }

    private void setUpAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8385601672345207/7590384284", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("AdError", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private void runAds() {
        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ViewItemActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();

                        }

                    });
                } else {

                }
            }
        }.start();
    }

}