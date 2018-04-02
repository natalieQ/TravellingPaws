package com.nailiqi.travellingpaws.Signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nailiqi.travellingpaws.Home.MainActivity;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.FirebaseMethods;

public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = "RegisterActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String append = "";

    private Context context = RegisterActivity.this;
    private ProgressBar progressBar;
    private EditText mEmail, mPassword,mUsername;
    private String email, password, username;
    private Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseMethods = new FirebaseMethods(context);

        progressBar = (ProgressBar) findViewById(R.id.progressbarRegister);
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mUsername = (EditText) findViewById(R.id.login_username);
        btnRegister = (Button) findViewById(R.id.btn_register);
        Log.d(TAG, "onCreate: starting");

        //set progress bar to invisible
        progressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        setupWidgets();
    }

    private void setupWidgets(){

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();

                if(email.length() == 0 || password.length() == 0){
                    Toast.makeText(context, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    //register new user
                    firebaseMethods.registerNewEmail(email, password, username);
                }
            }
        });

    }

    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //1st check: Make sure the username is not already in use
                            if(firebaseMethods.checkIfUsernameExists(username, dataSnapshot)){
                                append = myRef.push().getKey().substring(3,9);
                                Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                            }
                            username = username + append;

                            //add new user to the database
                            firebaseMethods.addNewUser(email, username, "", "");

                            Toast.makeText(context, "Signup successful", Toast.LENGTH_SHORT).show();

                            //redirect to home page
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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
