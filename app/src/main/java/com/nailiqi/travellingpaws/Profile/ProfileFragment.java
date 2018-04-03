package com.nailiqi.travellingpaws.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{
    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;
    private Context context;

    private ProgressBar mProgressBar;
    private CircleImageView profileImage;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationViewEx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        gridView = (GridView) view.findViewById(R.id.gridview);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavbar);
        context = getActivity();

        setupTopToolbar();
        setupBottomNavbar();

        return view;
    }

    /**
     * Bottom navbar setup
     */
    private void setupBottomNavbar() {
        Log.d(TAG, "setupBottomNavbar: Setting up Bottom navigation bar");

        //helper function to set up nav bar
        BottomNavbarHelper.setupBottomNavbar(bottomNavigationViewEx);
        //helper function to enable navigation
        BottomNavbarHelper.enableNavigation(context, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /**
     * Top Toolbar setup
     */
    private void setupTopToolbar(){

        //set toolbar as action bar
        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to acount setting");

                //navigate to Account Settings
                Intent intent = new Intent(context, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }


}
