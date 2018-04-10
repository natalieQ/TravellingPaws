package com.nailiqi.travellingpaws.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.nailiqi.travellingpaws.models.Photo;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nailiqi.travellingpaws.models.UserCombine;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment{

    private static final String TAG = "ViewPostFragment";

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private FirebaseMethods firebaseMethods;


    //widgets
    private ImageViewSquare mPostImage;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView mCaption, mUsername, mTimePosted;
    private ImageView goback, profileMenu, heart, comment, mProfileImage;

    private Photo mPhoto;
    private int mActivityNumber = 0;
    private UserAccount userAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpost, container,false);

        mPostImage = (ImageViewSquare) view.findViewById(R.id.post_image);
        mProfileImage = (ImageView) view.findViewById(R.id.profileImage);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavbar);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mTimePosted = (TextView) view.findViewById(R.id.image_time_posted);
        mUsername = (TextView) view.findViewById(R.id.username);
        goback = (ImageView) view.findViewById(R.id.goback);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        heart = (ImageView) view.findViewById(R.id.image_heart);
        comment = (ImageView) view.findViewById(R.id.image_comment);



        //retrieve photo from bundle
        try{
            mPhoto = getPhotoFromBundle();
            ImageLoaderHelper.setImage(mPhoto.getImg_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        setupFirebaseAuth();
//        setupWidgets();
        getPhotoInfoFromDatabase();
        setupBottomNavbar();


        return view;
    }

    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    private int getActivityNumFromBundle(){
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    private void setupWidgets(){

        //set up post created info
        String timestampDiff = getTimePosted();
        if(!timestampDiff.equals("0")){
            mTimePosted.setText(timestampDiff + " DAYS AGO");
        }else{
            mTimePosted.setText("TODAY");
        }

        //set user info - username + profileimage
        ImageLoaderHelper.setImage(userAccount.getProfile_image(), mProfileImage, null, "");
        mUsername.setText(userAccount.getUsername());
    }

    private String getTimePosted(){
        Log.d(TAG, "getTimePosted: getting posted days ago");

        String res = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getData_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            res = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            res = "0";
        }
        return res;
    }

    private void getPhotoInfoFromDatabase(){
        Log.d(TAG, "getPhotoDetails: get photo info.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account))
                .orderByChild("user_id")
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    userAccount = singleSnapshot.getValue(UserAccount.class);
                }
                setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
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
        BottomNavbarHelper.enableNavigation(getActivity(), bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();

        //pass in activity number from bundle
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }


    /**
     * Setup the firebase auth object
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
