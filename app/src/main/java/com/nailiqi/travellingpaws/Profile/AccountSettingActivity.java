package com.nailiqi.travellingpaws.Profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.Home.MainActivity;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;
import com.nailiqi.travellingpaws.Utils.PartStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountSettingActivity extends AppCompatActivity{

    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 3;
    private Context context = AccountSettingActivity.this;
    private PartStatePagerAdapter partStatePagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.d(TAG, "onCreate: starting");

        viewPager = (ViewPager) findViewById(R.id.containerViewpager);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        setupAccountSettingList();
        setupBottomNavbar();
        setupBackArrow();
        setupFragments();
        getIncomingIntent();
    }

    /**
     * set up account setting options
     */
    private void setupAccountSettingList(){
        //listview for fragments live in de AccountActivity
        ListView listView = (ListView) findViewById(R.id.listviewAccountSettings);

        //options of text strings
        List<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));
        options.add(getString(R.string.sign_out_fragment));

        //array adapter to render arraylist to listView
        ArrayAdapter adapter = new ArrayAdapter(AccountSettingActivity.this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        //add listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: go to fagment: " + position);
                setupViewpager(position);
            }
        });
    }

    /**
     * set up fragments
     */
    private void setupFragments(){
        partStatePagerAdapter = new PartStatePagerAdapter(getSupportFragmentManager());
        partStatePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));
        partStatePagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));
    }

    /**
     * set up viewpager
     */
    private void setupViewpager(int fragmentnum){
        //set current account setting layout to be invisible
        relativeLayout.setVisibility(View.GONE);

        //show fragment layout
        viewPager.setAdapter(partStatePagerAdapter);
        viewPager.setCurrentItem(fragmentnum);
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
        BottomNavbarHelper.enableNavigation(context, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /**
     * set up backarrow click listener
     */
    private void setupBackArrow(){
        ImageView goback = (ImageView) findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finish current activity(accountSetting) going back to pre activity
                finish();
            }
        });
    }

    /**
     * get imcoming Intent
     */
    private void getIncomingIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){

            //nagivate to edit_profile fragment under AccountSetting Activity
            setupViewpager(partStatePagerAdapter.getFragmentNum(getString(R.string.edit_profile_fragment)));
        }
    }
}
