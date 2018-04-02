package com.nailiqi.travellingpaws.Signin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nailiqi.travellingpaws.R;

public class SigninActivity extends AppCompatActivity{

    private static final String TAG = "SigninActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Log.d(TAG, "onCreate: starting");
    }
}
