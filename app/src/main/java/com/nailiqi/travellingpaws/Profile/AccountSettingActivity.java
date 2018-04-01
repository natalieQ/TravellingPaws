package com.nailiqi.travellingpaws.Profile;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.PartStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountSettingActivity extends AppCompatActivity{

    private static final String TAG = "AccountSettingActivity";
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
        setupBackArrow();
        setupFragments();
    }

    /**
     * set up account setting options
     */
    private void setupAccountSettingList(){
        ListView listView = (ListView) findViewById(R.id.listviewAccountSettings);

        //options of text strings
        List<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add(getString(R.string.sign_out));

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
        partStatePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile));
        partStatePagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out));
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
}
