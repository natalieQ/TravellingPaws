package com.nailiqi.travellingpaws.Share;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;
import com.nailiqi.travellingpaws.Utils.PartPagerAdapter;
import com.nailiqi.travellingpaws.Utils.Permissions;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private static final int ACTIVITY_NUM = 2;
    private static final int REQUEST_CODE = 1;

    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: starting");

        if(checkPermissionArray(Permissions.PERMISSIONS)){
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
            setupViewPager();
        }

//        setupBottomNavbar();
    }

    private void setupViewPager(){
        PartPagerAdapter adapter = new PartPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LibraryFragment());
        adapter.addFragment(new PhotoFragment());

        viewPager = (ViewPager) findViewById(R.id.containerViewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.bottomTabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Library");
        tabLayout.getTabAt(1).setText("Photo");

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
                ShareActivity.this,
                permissions,
                REQUEST_CODE
        );
    }

    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int request = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

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
        BottomNavbarHelper.enableNavigation(ShareActivity.this, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
