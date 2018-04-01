package com.nailiqi.travellingpaws.Home;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavbar();
        setupViewPager();
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.containerViewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        //set tab icon
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera_tab);

        //set up horizontal view for logo tab
        TabLayout.Tab logoTab = tabLayout.getTabAt(1);
        tabLayout.getTabAt(1).setIcon(R.drawable.travellingpaws);
        logoTab.setCustomView(R.layout.customtab);

        tabLayout.getTabAt(2).setIcon(R.drawable.ic_loc_tab);
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
        menuItem.setChecked(true);
    }
}
