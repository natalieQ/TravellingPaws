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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Signin.SigninActivity;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;
import com.nailiqi.travellingpaws.Utils.FirebaseMethods;
import com.nailiqi.travellingpaws.Utils.ImageGridViewAdapter;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;
import com.nailiqi.travellingpaws.models.Like;
import com.nailiqi.travellingpaws.models.Photo;
import com.nailiqi.travellingpaws.models.User;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nailiqi.travellingpaws.models.UserCombine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{
    private static final String TAG = "ProfileFragment";

    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener onGridImageSelectedListener;

    private static final int ACTIVITY_NUM = 3;
    private static int Grid_COL_NUM = 3;
    private Context context;

    private ProgressBar mProgressBar;
    private CircleImageView profileImage;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView mUsername, mDescription, mPetname, mPosts, mFollowers, mFollowing, mProfilebarUsername;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private FirebaseMethods firebaseMethods;

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
        mUsername = (TextView) view.findViewById(R.id.username);
        mDescription = (TextView) view.findViewById(R.id.description);
        mPetname = (TextView) view.findViewById(R.id.petname);
        mPosts = (TextView) view.findViewById(R.id.tvPost);
        mFollowers = (TextView) view.findViewById(R.id.tvFollower);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mProfilebarUsername = (TextView) view.findViewById(R.id.profilebar_username);
        context = getActivity();
        firebaseMethods = new FirebaseMethods(getActivity());

        setupTopToolbar();
        setupBottomNavbar();
        setupFirebaseAuth();
        setupGridView();

        //setup edit profile button
        TextView btnEditProfile = (TextView) view.findViewById(R.id.tvEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to edit profile fragment");
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try{
            onGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

    /**
     * setup widgets
     */
    private void setupWidgets(UserCombine userCombine){

        User user = userCombine.getUser();
        UserAccount account = userCombine.getUserAccount();

        //set profile image
        ImageLoaderHelper.setImage(account.getProfile_image(), profileImage, null, "");

        //set profile textview
        mUsername.setText(account.getUsername());
        mDescription.setText(account.getDescription());
        mPetname.setText(account.getPetname());
        mPosts.setText(String.valueOf(account.getPosts()));
        mFollowers.setText(String.valueOf((account.getFollowers())));
        mFollowing.setText(String.valueOf(account.getFollowing()));
        mProfilebarUsername.setText(account.getUsername());

    }

    /**
     * set up photo gridview
     */
    private void setupGridView(){
        Log.d(TAG, "setupGridView: Setting up user image grid.");

        final List<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //single event listner
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    //add to photo list
//                    photos.add(singleSnapshot.getValue(Photo.class));

                    //convert to hashmap for firebase
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get("caption").toString());
                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                    photo.setUser_id(objectMap.get("user_id").toString());
                    photo.setData_created(objectMap.get("data_created").toString());
                    photo.setImg_path(objectMap.get("img_path").toString());
                    photo.setGps_longitude(objectMap.get("gps_longitude").toString());
                    photo.setGps_longitude(objectMap.get("gps_latitude").toString());

                    List<Like> likelist = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child("likes").getChildren()){
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likelist.add(like);
                    }
                    photo.setLikes(likelist);
                    photos.add(photo);
                }
                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/Grid_COL_NUM;
                gridView.setColumnWidth(imageWidth);

                List<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < photos.size(); i++){
                    imgUrls.add(photos.get(i).getImg_path());
                }
                ImageGridViewAdapter adapter = new ImageGridViewAdapter(getActivity(),R.layout.section_grid_imageview,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                //ad grid image selected listner to the grid
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        onGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled.");
            }
        });
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


    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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

        //database ref listener
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: getting UserCombine");

                //get user_account + users info from database
                setupWidgets(firebaseMethods.getUserCombine(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
