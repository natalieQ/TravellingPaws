package com.nailiqi.travellingpaws.Share;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.FirebaseMethods;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    private static String append = "file:/";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private FirebaseMethods firebaseMethods;

    //GPS
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude;
    private double latitue;

    private EditText mCaption;

    private int imgCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        firebaseMethods = new FirebaseMethods(NextActivity.this);

        mCaption = (EditText) findViewById(R.id.nextDescription);

        Log.d(TAG, "onCreate: image selected is " + getIntent().getStringExtra(getString(R.string.selected_image)));

        setupFirebaseAuth();

        //go back
        ImageView goback = (ImageView) findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: going back.");
                finish();
            }
        });

        //share
        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: photo shared");

                //upload image to database
                String caption = mCaption.getText().toString();

                //from library
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    firebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imgCount, imgUrl,null);
                }
                //from camera
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    firebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imgCount, null,bitmap);
                }

            }
        });

        setupWidgets();

    }


    private void setupWidgets(){
        intent = getIntent();  //not new intent
        ImageView image = (ImageView) findViewById(R.id.shareImage);

        //from library
        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setupWidgets: got new image url: " + imgUrl);
            ImageLoaderHelper.setImage(imgUrl, image, null, append);
        }
        //from camera
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setupWidgets: got new bitmap");
            image.setImageBitmap(bitmap);
        }
    }

    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //database ref listener
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imgCount = firebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imgCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
