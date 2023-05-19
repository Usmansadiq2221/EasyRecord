package com.devtwist.easyrecord.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devtwist.easyrecord.Models.CustomerData;
import com.devtwist.easyrecord.Models.RecordData;
import com.devtwist.easyrecord.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateRecordActivity extends AppCompatActivity {

    private LinearLayout _cCnicPicLayout, _cTextLayout, _tInputLayout;
    private EditText _customerCnicNo, _customerName, _tId, _tPhoneNo, _tAmount;
    private Button _cnicSubmit, _recordSubmit;
    private ImageView _cCnicFront, _cCnicBack;
    private Spinner _tType, _company;
    private ArrayAdapter<CharSequence> adapter;
    private String cnicNo, customerName, tId, tPhone, tDate, company, shopUser, tAmount, tType, tTime;
    private DatabaseReference database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private RecordData recordData;
    private CustomerData  customerData;
    private Uri cnicFrontFilePAth, cnicBackFilePath;
    private Bitmap bitmap;
    private ProgressBar _cProgressBar;
    private TextView _cCnicNoError, _cCnicNameError, _cTIdError, _cPhoneNoError,
            _cTTypeError, _cCompanyError, _cAmountError, _cNetError, _uploadingStatus;

    private AdView _cRecordAdView;

    private static boolean isExist;
    private static char type;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        _cCnicPicLayout = findViewById(R.id._cCnicPicLayout);
        _cTextLayout = findViewById(R.id._cTextLayout);
        _tInputLayout = findViewById(R.id._tInputLayout);
        _customerCnicNo = findViewById(R.id._customerCnicNo);
        _customerName = findViewById(R.id._customerName);
        _tId = findViewById(R.id._tId);
        _tPhoneNo = findViewById(R.id._tPhoneNo);
        _tAmount = findViewById(R.id._tAmount);
        _cnicSubmit = findViewById(R.id._cnicSubmit);
        _recordSubmit = findViewById(R.id._recordSubmit);
        _cCnicFront = findViewById(R.id._cCnicFront);
        _cCnicBack = findViewById(R.id._cCnicBack);
        _tType = findViewById(R.id._tType);
        _company = findViewById(R.id._company);
        _cProgressBar = findViewById(R.id._cProgressBar);

        _cCnicNoError = findViewById(R.id._cCnicNoError);
        _cCnicNameError = findViewById(R.id._cCnicNameError);
        _cTIdError = findViewById(R.id._cTIdError);
        _cPhoneNoError = findViewById(R.id._cPhoneNoError);
        _cTTypeError = findViewById(R.id._cTTypeError);
        _cCompanyError = findViewById(R.id._cCompanyError);
        _cAmountError = findViewById(R.id._cAmountError);
        _cNetError = findViewById(R.id._cNetError);
        _uploadingStatus = findViewById(R.id._uploadingStatus);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        cnicNo = _customerCnicNo.getText().toString().trim();
        isExist = false;

        _cRecordAdView = findViewById(R.id._cRecordAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        _cRecordAdView.loadAd(adRequest);

        _customerCnicNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _uploadingStatus.setVisibility(View.GONE);
            }
        });

        adapter = ArrayAdapter.createFromResource(this,
                R.array.t_type_array, R.layout.spinner_style_file);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _tType.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.company_array, R.layout.spinner_style_file);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _company.setAdapter(adapter);

        _cnicSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_customerCnicNo.length()<13){
                    _cCnicNoError.setVisibility(View.VISIBLE);
                }
                else {
                    _uploadingStatus.setVisibility(View.GONE);
                    cnicNo = _customerCnicNo.getText().toString();
                    _cCnicNoError.setVisibility(View.GONE);
                    database.child("Customers").child(cnicNo).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            if (task.isSuccessful()) {
                                DataSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    isExist = true;
                                    database.child("Customers").child(cnicNo).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            CustomerData customerData = snapshot.getValue(CustomerData.class);
                                            _cnicSubmit.setVisibility(View.GONE);
                                            _customerName.setText(customerData.getCustomerName());
                                            _customerName.setVisibility(View.VISIBLE);
                                            _tInputLayout.setVisibility(View.VISIBLE);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {
                                    _cnicSubmit.setVisibility(View.GONE);
                                    _customerName.setVisibility(View.VISIBLE);
                                    _cCnicPicLayout.setVisibility(View.VISIBLE);
                                    _cTextLayout.setVisibility(View.VISIBLE);
                                    _tInputLayout.setVisibility(View.VISIBLE);

                                }
                            } else {
                                Toast.makeText(CreateRecordActivity.this, "Network Problem\nCheck your internet connection", Toast.LENGTH_LONG).show();
                                Log.d("User Fonud Error", task.getException().getMessage().toString());
                            }
                        }
                    });
                }
            }
        });

        _cCnicFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage('f');
            }
        });

        _cCnicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage( 'b');
            }
        });

        _recordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_tId.getText().length()>=11 && _tPhoneNo.getText().length()==11 && !_company.getSelectedItem().toString().equals("Select Company")
                        && !_tType.getSelectedItem().toString().equals("Select Type") && _tAmount.getText().toString().length()>1){
                    _recordSubmit.setVisibility(View.GONE);
                    _cProgressBar.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    if (isExist){
                        createRecord();
                    }
                    else{
                        if(_customerName.getText().length()>3 && _customerCnicNo.getText().length()==13 && cnicFrontFilePAth.toString().length()>1
                                && cnicBackFilePath.toString().length()>1) {
                            storageReference = storage.getReference().child("Customers").child(_customerCnicNo.getText().toString() + "fCNICPic");
                            storageReference.putFile(cnicFrontFilePAth).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    storageReference = storage.getReference().child("Customers").child(_customerCnicNo.getText().toString() + "fCNICPic");
                                                    storageReference.putFile(cnicBackFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    try {
                                                                        cnicNo = _customerCnicNo.getText().toString().trim();
                                                                        customerName = _customerName.getText().toString().trim();
                                                                        customerData = new CustomerData(cnicNo, customerName, cnicFrontFilePAth.toString(), cnicBackFilePath.toString());
                                                                        database.child("Customers").child(cnicNo).setValue(customerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                createRecord();
                                                                            }
                                                                        });
                                                                    } catch (Exception e) {
                                                                        Log.i("CDU Error", e.getMessage().toString());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        } catch (Exception e) {
                                            Log.i("CDPU Error", e.getMessage().toString());
                                        }
                                    }
                                    else{
                                        _recordSubmit.setVisibility(View.VISIBLE);
                                        _cProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(CreateRecordActivity.this, "Network Problem\nCheck your internet connection!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            if (_customerName.getText().length()<3){
                                _cCnicNameError.setVisibility(View.VISIBLE);
                            } else{
                                _cCnicNameError.setVisibility(View.GONE);
                            }
                            if (_customerCnicNo.length()<13){
                                _cCnicNoError.setVisibility(View.VISIBLE);
                            } else{
                                _cCnicNoError.setVisibility(View.GONE);
                            }
                            if (cnicFrontFilePAth.toString().length()<1){
                                Toast.makeText(CreateRecordActivity.this, "CNIC Front Picture\nNot Found.", Toast.LENGTH_SHORT).show();
                            }
                            if (cnicBackFilePath.toString().length()<1){
                                Toast.makeText(CreateRecordActivity.this, "CNIC Back Picture\nNot Found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else {
                    if (_tId.getText().toString().length() < 11) {
                        _cTIdError.setVisibility(View.VISIBLE);
                    }  else{
                        _cTIdError.setVisibility(View.GONE);
                    }
                    if (_tPhoneNo.getText().toString().length() < 11) {
                        _cPhoneNoError.setVisibility(View.VISIBLE);
                    }else{
                        _cPhoneNoError.setVisibility(View.GONE);
                    }
                    if (_company.getSelectedItem().toString().equals("Select Company")) {
                        _cCompanyError.setVisibility(View.VISIBLE);
                    } else {
                        _cCompanyError.setVisibility(View.GONE);
                    }
                    if (_tType.getSelectedItem().toString().equals("Select Type")) {
                        _cTTypeError.setVisibility(View.VISIBLE);
                    } else{
                        _cTTypeError.setVisibility(View.GONE);
                    }
                    if (_tAmount.getText().toString().length() < 1) {
                        _cAmountError.setVisibility(View.VISIBLE);
                    }else{
                        _cAmountError.setVisibility(View.GONE);
                    }
                }


            }
        });


    }

    private void createRecord() {
        try {

            tId = _tId.getText().toString().trim();
            tPhone = _tPhoneNo.getText().toString().trim();
            tDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            tTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            company = _company.getSelectedItem().toString().trim();
            shopUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            tAmount = _tAmount.getText().toString().trim();
            tType = _tType.getSelectedItem().toString().trim();
            Date date = new Date();
            double timeStamp = date.getTime();

            recordData = new RecordData(cnicNo, tId, tPhone, tDate,
                    company, shopUser, tAmount, tType,tTime,timeStamp);
            database.child("Transactions").child(tId).setValue(recordData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    _customerCnicNo.setText("");
                    _customerName.setText("");
                    _tId.setText("");
                    _tPhoneNo.setText("");
                    _tAmount.setText("");
                    _cCnicFront.setImageResource(R.drawable.placeholder);
                    _cCnicBack.setImageResource(R.drawable.placeholder);
                    cnicFrontFilePAth = null;
                    cnicBackFilePath = null;

                    _customerName.setVisibility(View.GONE);
                    _cnicSubmit.setVisibility(View.VISIBLE);
                    _cCnicPicLayout.setVisibility(View.GONE);
                    _cTextLayout.setVisibility(View.GONE);
                    _tInputLayout.setVisibility(View.GONE);
                    _cProgressBar.setVisibility(View.GONE);
                    _uploadingStatus.setVisibility(View.VISIBLE);
                    _recordSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(CreateRecordActivity.this, "Transaction Successfull", Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            Log.d("Create Record Error", e.getMessage().toString());
        }
    }

    private void captureImage(char t) {
        Dexter.withActivity(CreateRecordActivity.this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

                type = t;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,2)
                        .start(CreateRecordActivity.this);

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(CreateRecordActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(type=='f') {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    try {
                        cnicFrontFilePAth = result.getUri();
                        InputStream inputStream = getContentResolver().openInputStream(cnicFrontFilePAth);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        _cCnicFront.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
        else if (type=='b'){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    try {
                        cnicBackFilePath = result.getUri();
                        InputStream inputStream = getContentResolver().openInputStream(cnicBackFilePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        _cCnicBack.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }

    }

}