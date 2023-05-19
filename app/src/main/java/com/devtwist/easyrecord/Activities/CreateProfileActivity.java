package com.devtwist.easyrecord.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.devtwist.easyrecord.Models.Userdata;
import com.devtwist.easyrecord.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateProfileActivity extends AppCompatActivity {

    private ImageView _cnicFrontPic, _cnicBackPic,_profileimage, _signupBackButton;
    private EditText _username, _shopName, _cnicNo, _address;
    private Uri fCnicFilePath, bCnicFilePath, proPicFilePath;
    private Bitmap bitmap;
    private String token, uId, username, shopName, cnicNo, province,
            city,address, phoneNo, errorMessage;
    private boolean isActive;
    private Spinner _province,_city;
    private Button _signUpButton;
    private ArrayAdapter<CharSequence> adapter;
    private Userdata userdata;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Intent intent;
    private FirebaseAuth mAuth;
    private static int ratioX,ratioY;
    private static char type;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        _signupBackButton = findViewById(R.id._signupBackButton);
        _cnicFrontPic = findViewById(R.id._cnicFrontPic);
        _cnicBackPic = findViewById(R.id._cnicBackPic);
        _profileimage = findViewById(R.id._profileimage);
        _username = findViewById(R.id._username);
        _shopName = findViewById(R.id._shopName);
        _cnicNo = findViewById(R.id._cnicNo);
        _province = findViewById(R.id._province);
        _city = findViewById(R.id._city);
        _address = findViewById(R.id._address);
        _signUpButton = findViewById(R.id._signUpButton);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        _signupBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage(1,1,'p');
            }
        });

        _cnicFrontPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(3,2,'f');
            }
        });

        _cnicBackPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(3,2, 'b');
            }
        });

        adapter = ArrayAdapter.createFromResource(this,
                R.array.province_array, R.layout.spinner_style_file);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _province.setAdapter(adapter);
        _province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                province = _province.getSelectedItem().toString();
                if (i==0){
                    adapter = ArrayAdapter.createFromResource(CreateProfileActivity.this,
                            R.array.punjab_array, R.layout.spinner_style_file);
                }
                else if (i==1){
                    adapter = ArrayAdapter.createFromResource(CreateProfileActivity.this,
                            R.array.balochistan_array, R.layout.spinner_style_file);
                }
                else if (i==2){
                    adapter = ArrayAdapter.createFromResource(CreateProfileActivity.this,
                            R.array.kpk_array, R.layout.spinner_style_file);
                }
                else if (i==3){
                    adapter = ArrayAdapter.createFromResource(CreateProfileActivity.this,
                            R.array.sindh_array, R.layout.spinner_style_file);
                }
                else{
                    adapter = ArrayAdapter.createFromResource(CreateProfileActivity.this,
                            R.array.punjab_array, R.layout.spinner_style_file);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                _city.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = _username.getText().toString().trim();

                if ( username.length()<1 && _shopName.getText().length()<1 && _cnicNo.getText().length()<1
                        && _address.getText().length()<1) {

                    Toast.makeText(CreateProfileActivity.this, "Enter All Values", Toast.LENGTH_SHORT).show();

                } else if (username.length()>3 && _shopName.getText().length()>3 && _cnicNo.getText().length()>12
                        && _address.getText().length()>1) {

                    if (proPicFilePath.toString().length()>1){
                        uploadToFirebase();
                    }
                    else{
                        Toast.makeText(CreateProfileActivity.this, "Profile Picture not found", Toast.LENGTH_SHORT).show();
                    }
                } else {


                    if (username.length()<3){
                        errorMessage = "Invalid First name";
                    }

                    if (_shopName.getText().length()<3){
                        errorMessage = "Invalid Last name";
                    }
                    if (_cnicNo.getText().length()<13){
                        errorMessage = "Invalid CNIC No";
                    }
                    if (_address.getText().length()<10){
                        errorMessage = "Invalid Address No";
                    }
                    Toast.makeText(CreateProfileActivity.this, "testing", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void captureImage(int x, int y, char t) {
        Dexter.withActivity(CreateProfileActivity.this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                ratioX=x;
                ratioY=y;
                type = t;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(ratioX,ratioY)
                        .start(CreateProfileActivity.this);

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(CreateProfileActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                        fCnicFilePath = result.getUri();
                        InputStream inputStream = getContentResolver().openInputStream(fCnicFilePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        _cnicFrontPic.setImageBitmap(bitmap);
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
                        bCnicFilePath = result.getUri();
                        InputStream inputStream = getContentResolver().openInputStream(bCnicFilePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        _cnicBackPic.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }
        else if (type=='p'){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    proPicFilePath = result.getUri();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(proPicFilePath);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        _profileimage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

    }

    private void uploadToFirebase() {
        username = _username.getText().toString().trim();
        shopName = _shopName.getText().toString().trim();
        cnicNo = _cnicNo.getText().toString().trim();
        phoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        province = _province.getSelectedItem().toString().trim();
        address = _address.getText().toString().trim();
        city = _city.getSelectedItem().toString().trim();
        isActive = false;


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Creating Profile");
        dialog.setCancelable(false);
        dialog.show();
        if(proPicFilePath.toString().length()>1 && fCnicFilePath.toString().length()>1 && bCnicFilePath.toString().length()>1) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference uploadfile = storage.getReference().child("Profile").child("ProfilePic" + new Random());
            uploadfile.putFile(proPicFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri profileUri) {
                            StorageReference uploadcnicfile = storage.getReference().child("Cnic").child(phoneNo +"fCNICPic");
                            uploadcnicfile.putFile(fCnicFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    uploadcnicfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri fCnicUri) {
                                            StorageReference uploadcnicfile = storage.getReference().child("Cnic").child(phoneNo +"bCNICPic");
                                            uploadcnicfile.putFile(bCnicFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    uploadcnicfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri bCnicUri) {
                                                            uId = mAuth.getCurrentUser().getUid().toString();
                                                            userdata = new Userdata(uId, cnicNo, username, shopName, phoneNo, province, city, address, profileUri.toString(), fCnicUri.toString(), bCnicUri.toString(), isActive, token);
                                                            databaseReference.child("Users").child(uId).setValue(userdata);

                                                            intent = new Intent(CreateProfileActivity.this, RecordActivity.class);
                                                            dialog.dismiss();
                                                            Toast.makeText(CreateProfileActivity.this, "Profile Successfully Created!", Toast.LENGTH_SHORT).show();
                                                            preferences = getSharedPreferences("MyData", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putBoolean("isProfileCreated", true);
                                                            editor.commit();
                                                            startActivity(intent);

                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    });
                                }
                            });

                            _username.setText("Enter First Name");
                            _shopName.setText("Enter Last Name");
                            _cnicNo.setText("Enter CNIC No");
                            _address.setText("");
                            _profileimage.setImageResource(R.drawable.enter_profile_icon);

                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    dialog.setMessage("Upload :" + (int) percent + "%");
                }
            });

        }else {
            if (proPicFilePath.toString().length()<1) {
                Toast.makeText(CreateProfileActivity.this, "Profile not selected!", Toast.LENGTH_SHORT).show();
            }
            else if (fCnicFilePath.toString().length()<1){
                Toast.makeText(CreateProfileActivity.this, "CNIC Front Pic not Selected!", Toast.LENGTH_SHORT).show();
            }
            else if (bCnicFilePath.toString().length()<1){
                Toast.makeText(CreateProfileActivity.this, "CNIC Back Pic not Selected!", Toast.LENGTH_SHORT).show();
            }
        }


    }


}