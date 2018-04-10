package com.nailiqi.travellingpaws.Home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Share.ShareActivity;
import com.nailiqi.travellingpaws.Signin.SigninActivity;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;
import com.nailiqi.travellingpaws.Utils.PartPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context context = MainActivity.this;
    private static final int REQUEST_CODE = 0;  //camera fragment

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager viewPager;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.containerViewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //initialize firebase auth
        setupFirebaseAuth();

        initImageLoader();

        setupBottomNavbar();
        setupViewPager();


        //mAuth.signOut();
    }

    /**
     * checks to see if user is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(context, SigninActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        checkCurrentUser(user);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Middle view pager setup + top tabs
     */
    private void setupViewPager(){
        PartPagerAdapter adapter = new PartPagerAdapter(getSupportFragmentManager());

        //add fragment tab
        adapter.addFragment(new CameraFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new LocationFragment());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        //set tab icon
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera_tab);

        //set up horizontal view for logo tab
        TabLayout.Tab logoTab = tabLayout.getTabAt(1);
        tabLayout.getTabAt(1).setIcon(R.drawable.travellingpaws);
        logoTab.setCustomView(R.layout.customtab);

        tabLayout.getTabAt(2).setIcon(R.drawable.ic_loc_tab);

        //default set to tab 1 - homefeed
        viewPager.setCurrentItem(1);
    }

    public int getCurTabNum(){
        return viewPager.getCurrentItem();
    }

    public boolean checkPermissionArray(String[] permissions){
        Log.d(TAG, "checkPermissionArray: check permission array");

        for(int i = 0; i< permissions.length; i++){
            if(!checkPermissions(permissions[i])){
                return false;
            }
        }
        return true;

    }

    public void verifyPermissions(String[] permissions){
        ActivityCompat.requestPermissions(
                MainActivity.this,
                permissions,
                REQUEST_CODE
        );
    }

    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int request = ActivityCompat.checkSelfPermission(MainActivity.this, permission);

        if(request != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: Permission was granted for: " + permission);
            return true;
        }
    }


    /**
     * Bottom navbar setup
     */
    private void setupBottomNavbar() {
        Log.d(TAG, "setupBottomNavbar: Setting up Bottom navigation bar");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavbar);
        //helper function to set up nav bar
        BottomNavbarHelper.setupBottomNavbar(bottomNavigationViewEx);
        //helper function to enable navigation
        BottomNavbarHelper.enableNavigation(MainActivity.this, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true); //mark current tab checked
    }

    private void initImageLoader(){
        ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper(context);
        ImageLoader.getInstance().init(imageLoaderHelper.getConfig());
    }
}
