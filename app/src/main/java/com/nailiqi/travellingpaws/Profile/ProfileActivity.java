package com.nailiqi.travellingpaws.Profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static int ACTIVITY_NUM = 4;
    private Context context = ProfileActivity.this;
    private static int Grid_COL_NUM = 3;

    private ProgressBar progressBar;
    private ImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: starting");

        setupBottomNavbar();
        setupTopToolbar();
        setupWidgets();
        setupProfileImage();

        tempImages();
    }

    /**
     * Top Toolbar setup
     */
    private void setupTopToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        //set toolbar as action bar
        setSupportActionBar(toolbar);
        
        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to acount setting");

                //navigate to Account Settings
                Intent intent = new Intent(ProfileActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * set up widgets
     */
    private void setupWidgets(){
        progressBar = (ProgressBar)findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.GONE);
        profileImage = (ImageView) findViewById(R.id.profileImage);

    }

    /**
     * set up profile image
     */
    private void setupProfileImage(){
        String imgUrl = "http://www.worldclassbichons.com/uploads/3/4/6/0/34608590/edited/img-2647_1.jpeg";
        ImageLoaderHelper.setImage(imgUrl, profileImage, progressBar, "");

    }

    private void tempImages(){
        List<String> imgUrls = new ArrayList<>();

        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");


        setupGridView(imgUrls);
    }

    /**
     * set up image gridview
     */
    private void setupGridView(List<String> imgUrls){
        GridView gridView = (GridView) findViewById(R.id.gridview);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / Grid_COL_NUM;
        gridView.setColumnWidth(imageWidth);

        ImageGridViewAdapter adapter = new ImageGridViewAdapter(context, R.layout.section_grid_imageview, "", imgUrls);
        gridView.setAdapter(adapter);
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
        BottomNavbarHelper.enableNavigation(ProfileActivity.this, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
