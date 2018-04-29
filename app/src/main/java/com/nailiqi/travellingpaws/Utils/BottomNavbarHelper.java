package com.nailiqi.travellingpaws.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.Fav.FavActivity;
import com.nailiqi.travellingpaws.Home.MainActivity;
import com.nailiqi.travellingpaws.Map.MapActivity;
import com.nailiqi.travellingpaws.Profile.ProfileActivity;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Share.ShareActivity;

public class BottomNavbarHelper {
    private static final String TAG = "BottomNavbarHelper";

    public static void setupBottomNavbar(BottomNavigationViewEx bottomNavigationViewEx){
        //setup to static
        Log.d(TAG, "setupBottomNavbar: Setting up bottom navBar");
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        Intent intentHome = new Intent(context, MainActivity.class);   //ACTIVITY_NUM = 0
                        context.startActivity(intentHome);
                        break;

                    case R.id.ic_paw:
                        Intent intentMap = new Intent(context, MapActivity.class);     //ACTIVITY_NUM = 1
                        context.startActivity(intentMap);
                        break;

                    case R.id.ic_camera:
                        Intent intentShare = new Intent(context, ShareActivity.class);  //ACTIVITY_NUM = 2
                        context.startActivity(intentShare);
                        break;
                        //favorite feature not implemented yet
//                    case R.id.ic_favorite:
//                        Intent intentFav = new Intent(context, FavActivity.class);
//                        context.startActivity(intentFav);
//                        break;

                    case R.id.ic_profile:
                        Intent intentProfile = new Intent(context, ProfileActivity.class);   //ACTIVITY_NUM = 3
                        context.startActivity(intentProfile);
                        break;
                }
                return false;
            }
        });

    }
}
