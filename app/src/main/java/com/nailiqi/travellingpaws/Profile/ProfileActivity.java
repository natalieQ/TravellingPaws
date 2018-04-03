package com.nailiqi.travellingpaws.Profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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

        setupFragments();


//        setupTopToolbar();
//
//        setupProfileImage();
//
//        tempImages();
    }

    private void setupFragments(){
        Log.d(TAG, "setupFragments: inflating " + getString(R.string.profile_fragment));

        ProfileFragment fragment =  new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        //repalce current container with profile fragment
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }

//

//
//    /**
//     * set up profile image
//     */
//    private void setupProfileImage(){
//        String imgUrl = "http://www.worldclassbichons.com/uploads/3/4/6/0/34608590/edited/img-2647_1.jpeg";
//        ImageLoaderHelper.setImage(imgUrl, profileImage, progressBar, "");
//
//    }
//
//    private void tempImages(){
//        List<String> imgUrls = new ArrayList<>();
//
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//        imgUrls.add("http://cuteoverload.files.wordpress.com/2010/04/4531367433_7dee6bfbaf_o.jpg?w=560&h=373");
//
//
//        setupGridView(imgUrls);
//    }
//
//    /**
//     * set up image gridview
//     */
//    private void setupGridView(List<String> imgUrls){
//        GridView gridView = (GridView) findViewById(R.id.gridview);
//
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth / Grid_COL_NUM;
//        gridView.setColumnWidth(imageWidth);
//
//        ImageGridViewAdapter adapter = new ImageGridViewAdapter(context, R.layout.section_grid_imageview, "", imgUrls);
//        gridView.setAdapter(adapter);
//    }
//
//


}
