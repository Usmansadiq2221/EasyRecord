package com.devtwist.easyrecord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.devtwist.easyrecord.Adapters.SearchAdapter;
import com.devtwist.easyrecord.Models.RecordData;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchRecordActivity extends AppCompatActivity {

    private Spinner _searchType;
    private EditText _sCnicNo, _sTId, _sTPhoneNo, _tDate;
    private TextView _searchErrorText, _sNetworkErrorText;
    private Button _searchSubmit;
    private InterstitialAd mInterstitialAd;
    private String tId, cnicNo, phoneNo, tDate, uId;
    private ShimmerRecyclerView _preRecordList;
    private SearchAdapter searchListAdapter;
    private List<RecordData> recordDataList;
    private ArrayAdapter adapter;
    private char type;
    private static String headingText;
    private DatabaseReference databaseReference;
    private AdView _sRecordAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        setUpAds();

        _searchType = findViewById(R.id._searchType);
        _sCnicNo = findViewById(R.id._sCnicNo);
        _sTId = findViewById(R.id._sTId);
        _sTPhoneNo = findViewById(R.id._sTPhoneNo);
        _tDate = findViewById(R.id._tDate);
        _searchErrorText = findViewById(R.id._searchErrorText);
        _sNetworkErrorText = findViewById(R.id._sNetworkErrorText);
        _searchSubmit = findViewById(R.id._searchSubmit);
        _preRecordList = findViewById(R.id._preRecordList);
        _sRecordAdView = findViewById(R.id._sRecordAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        _sRecordAdView.loadAd(adRequest);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        _preRecordList.setLayoutManager(linearLayoutManager);
        recordDataList = new ArrayList<>();
        searchListAdapter = new SearchAdapter(getApplicationContext(), recordDataList,headingText);

        _preRecordList.setAdapter(searchListAdapter);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.search_array, R.layout.spinner_style_file);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _searchType.setAdapter(adapter);

        _searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    type = 'i';
                    _searchSubmit.setVisibility(View.VISIBLE);
                    _sTId.setVisibility(View.VISIBLE);
                    _tDate.setVisibility(View.GONE);
                    _sCnicNo.setVisibility(View.GONE);
                    _sTPhoneNo.setVisibility(View.GONE);
                }
                else if (position == 2){
                    type = 'd';
                    _searchSubmit.setVisibility(View.VISIBLE);
                    _sTId.setVisibility(View.GONE);
                    _tDate.setVisibility(View.VISIBLE);
                    _sCnicNo.setVisibility(View.GONE);
                    _sTPhoneNo.setVisibility(View.GONE);
                }
                else if (position == 3){
                    type = 'c';
                    _searchSubmit.setVisibility(View.VISIBLE);
                    _sTId.setVisibility(View.GONE);
                    _tDate.setVisibility(View.GONE);
                    _sCnicNo.setVisibility(View.VISIBLE);
                    _sTPhoneNo.setVisibility(View.GONE);
                }
                else if (position ==4){
                    type = 'p';
                    _searchSubmit.setVisibility(View.VISIBLE);
                    _sTId.setVisibility(View.GONE);
                    _tDate.setVisibility(View.GONE);
                    _sCnicNo.setVisibility(View.GONE);
                    _sTPhoneNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _searchSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAds();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);

                if (type == 'i' && _sTId.getText().length()<1){
                    _searchErrorText.setText("enter transaction id");
                    _searchErrorText.setVisibility(View.VISIBLE);
                }
                else if (type == 'i' && _sTId.getText().length()>1){
                    _searchErrorText.setVisibility(View.GONE);
                    searchRecord('i');
                }

                if (type == 'd' && _tDate.getText().length()<1){
                    _searchErrorText.setText("enter transaction date");
                    _searchErrorText.setVisibility(View.VISIBLE);
                }
                else if (type == 'd' && _tDate.getText().length()>1){
                    _searchErrorText.setVisibility(View.GONE);
                    searchRecord('d');
                }

                if (type == 'c' && _sCnicNo.getText().toString().length()<1){
                    _searchErrorText.setText("enter CNIC no");
                    _searchErrorText.setVisibility(View.VISIBLE);
                }
                else if (type == 'c' && _sCnicNo.getText().length()>1){
                    _searchErrorText.setVisibility(View.GONE);
                    searchRecord('c');
                }

                if (type == 'p' && _sTPhoneNo.getText().length()<1){
                    _searchErrorText.setText("enter phone no");
                    _searchErrorText.setVisibility(View.VISIBLE);
                }
                else if (type == 'p' && _sTPhoneNo.getText().length()>1){
                    _searchErrorText.setVisibility(View.GONE);
                    searchRecord('p');
                }
            }

        });

    }

    private void searchRecord(char type) {
        /*recordDataList = new ArrayList<>();
        searchListAdapter = new SearchAdapter(getApplicationContext(), recordDataList,uId);
        _preRecordList.setAdapter(searchListAdapter);

         */
        _preRecordList.showShimmerAdapter();
        _preRecordList.setVisibility(View.VISIBLE);
        if (type == 'i'){
            headingText = tId = _sTId.getText().toString().trim();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Transactions");
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recordDataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        RecordData recordData = dataSnapshot.getValue(RecordData.class);
                        if (recordData.gettId().equals(tId) && recordData.getShopUser().equals(uId)){
                            recordDataList.add(recordData);
                        }
                    }
                    if (recordDataList.size()==0){
                        _searchErrorText.setText("Invalid transaction id");
                        _searchErrorText.setVisibility(TextView.VISIBLE);
                    }
                    recordDataList.sort(Comparator.comparing(RecordData::getTimestamp));
                    _preRecordList.hideShimmerAdapter();
                    searchListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (type == 'd'){
            headingText = tDate = _tDate.getText().toString().trim();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Transactions");
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recordDataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        RecordData recordData = dataSnapshot.getValue(RecordData.class);
                        if (recordData.gettDate().equals(tDate) && recordData.getShopUser().equals(uId)){
                            recordDataList.add(recordData);
                        }
                    }
                    if (recordDataList.size()==0){
                        _searchErrorText.setText("Invalid date");
                        _searchErrorText.setVisibility(TextView.VISIBLE);
                    }
                    recordDataList.sort(Comparator.comparing(RecordData::getTimestamp));
                    _preRecordList.hideShimmerAdapter();
                    searchListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (type == 'c'){
            headingText = cnicNo = _sCnicNo.getText().toString().trim();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Transactions");
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recordDataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        RecordData recordData = dataSnapshot.getValue(RecordData.class);
                        if (recordData.getCustomerCnicNo().equals(cnicNo) && recordData.getShopUser().equals(uId)){
                            recordDataList.add(recordData);
                        }
                    }
                    if (recordDataList.size()==0){
                        _searchErrorText.setText("Invalid CNIC no");
                        _searchErrorText.setVisibility(TextView.VISIBLE);
                    }
                    recordDataList.sort(Comparator.comparing(RecordData::getTimestamp));
                    _preRecordList.hideShimmerAdapter();
                    searchListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (type == 'p'){
            headingText = phoneNo = _sTPhoneNo.getText().toString().trim();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Transactions");
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recordDataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        RecordData recordData = dataSnapshot.getValue(RecordData.class);
                        if (recordData.gettPhone().equals(phoneNo) && recordData.getShopUser().equals(uId)){
                            recordDataList.add(recordData);
                        }
                    }
                    if (recordDataList.size()==0){
                        _searchErrorText.setText("Invalid phone no");
                        _searchErrorText.setVisibility(TextView.VISIBLE);
                    }
                    recordDataList.sort(Comparator.comparing(RecordData::getTimestamp));
                    _preRecordList.hideShimmerAdapter();
                    searchListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                    mInterstitialAd.show(SearchRecordActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            setUpAds();
                        }

                    });
                } else {

                }
            }
        }.start();
    }

}