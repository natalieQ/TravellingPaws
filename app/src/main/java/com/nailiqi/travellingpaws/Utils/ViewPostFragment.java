package com.nailiqi.travellingpaws.Utils;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.nailiqi.travellingpaws.models.Like;
import com.nailiqi.travellingpaws.models.Photo;
import com.nailiqi.travellingpaws.models.User;
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
    private TextView mCaption, mUsername, mTimePosted,userLikes;
    private ImageView goback, profileMenu, hollowHeart, solidHeart,  comment, mProfileImage;

    private Photo mPhoto;
    private int mActivityNumber = 0;
    private UserAccount userAccount;
    private GestureDetector gestureDetector;
    private Heart heart;
    private boolean isLikedByCurUser;
    private StringBuilder users;
    private String userLikesDisplay;

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
        hollowHeart = (ImageView) view.findViewById(R.id.image_heart);
        solidHeart = (ImageView) view.findViewById(R.id.image_heart_solid);
        comment = (ImageView) view.findViewById(R.id.image_comment);
        userLikes = (TextView) view.findViewById(R.id.image_likes);

        //toggle likes
        hollowHeart.setVisibility(View.VISIBLE);
        solidHeart.setVisibility(View.GONE);
        heart = new Heart(hollowHeart,solidHeart);
        gestureDetector = new GestureDetector(getActivity(), new GestureListener());

        //retrieve photo from bundle
        try{
            mPhoto = getPhotoFromBundle();
            ImageLoaderHelper.setImage(mPhoto.getImg_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();
            getPhotoInfoFromDatabase();
            getLikes();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        setupFirebaseAuth();;
        setupBottomNavbar();

        //setup goback button
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finish current activity going back to pre activity
                getActivity().finish();
            }
        });

        return view;
    }


    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            //query likes from photos node
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        //photoid
                        String keyID = singleSnapshot.getKey();

                        //check if user already liked the photo
                        if(isLikedByCurUser &&
                                singleSnapshot.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            //remove from photos node
                            dbRef.child(getString(R.string.dbname_photos))
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            //remove from user_photos node
                            dbRef.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //get userid
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            heart.toggleLike();
                            getLikes();
                        }
                        //user has not liked the photo
                        else if(!isLikedByCurUser){
                            //add new like
                            addNewLike();
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void getLikes(){
        Log.d(TAG, "getLikes: get likes");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //find likes from photo
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    //find userid from users
                    Query query = reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild("user_id")
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: like from user: " +
                                        singleSnapshot.getValue(User.class).getUsername());

                                users.append(singleSnapshot.getValue(User.class).getUsername());
                                users.append(",");
                            }

                            //retrieve all users
                            String[] splitUsers = users.toString().split(",");

                            if(users.toString().contains(userAccount.getUsername() + ",")){
                                isLikedByCurUser = true;
                            }else{
                                isLikedByCurUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1){
                                userLikesDisplay = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                userLikesDisplay = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                userLikesDisplay = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            }
                            else if(length > 3){
                                userLikesDisplay = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + (splitUsers.length - 2) + " others";
                            }
                            Log.d(TAG, "onDataChange: likesdisplay: " + userLikesDisplay);
                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    userLikesDisplay = "";
                    isLikedByCurUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: add new like");

        //get new like id
        String newLikeID = dbRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        dbRef.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        heart.toggleLike();
        getLikes();
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
        //set userlikes
        userLikes.setText(userLikesDisplay.toString());
        //set image caption
        mCaption.setText(mPhoto.getCaption().toString());

        //toggle heart based on is the image is like by current user
        if(isLikedByCurUser){
            hollowHeart.setVisibility(View.GONE);
            solidHeart.setVisibility(View.VISIBLE);
            solidHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            hollowHeart.setVisibility(View.VISIBLE);
            solidHeart.setVisibility(View.GONE);
            hollowHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

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
//                setupWidgets();
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
