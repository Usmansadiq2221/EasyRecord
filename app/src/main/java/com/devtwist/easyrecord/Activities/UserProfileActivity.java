package com.devtwist.easyrecord.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devtwist.easyrecord.Models.Userdata;
import com.devtwist.easyrecord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView _profileImage;
    private TextView _userName, _userIdCard, _viewPolicy, _viewTerms;
    private LinearLayout _profileLayout;
    private ProgressBar _profileprogress;
    private String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        _profileImage = findViewById(R.id._profileimage);
        _userName = findViewById(R.id._userName);
        _userIdCard = findViewById(R.id._userIdCard);
        _viewPolicy = findViewById(R.id._viewPolicy);
        _viewTerms = findViewById(R.id._viewTerms);
        _profileLayout = findViewById(R.id._profileLayout);
        _profileprogress = findViewById(R.id._profileprogress);

        myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userdata userdata = snapshot.getValue(Userdata.class);
                try {
                    if (userdata.getProfilePic().length()>0){
                        Picasso.get().load(userdata.getProfilePic()).into(_profileImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                _userName.setText(userdata.getUsername());
                _userIdCard.setText(userdata.getCnicNo());
                _profileprogress.setVisibility(View.GONE);
                _profileLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        _viewPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", "p");
                Intent intent = new Intent(UserProfileActivity.this, ViewItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        _viewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", "t");
                Intent intent = new Intent(UserProfileActivity.this, ViewItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}