package com.nailiqi.travellingpaws.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nailiqi.travellingpaws.Home.MainActivity;
import com.nailiqi.travellingpaws.Profile.ProfileActivity;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.models.Like;
import com.nailiqi.travellingpaws.models.Photo;
import com.nailiqi.travellingpaws.models.User;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainListAdapter extends ArrayAdapter<Photo> {

    private static final String TAG = "MainListAdapter";

    private LayoutInflater inflater;
    private int layoutResource;
    private Context context;
    private String currentUsername = "";

    //firebase
    private DatabaseReference dbRef;


    public MainListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.context = context;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    static class ViewHolder{
        CircleImageView mprofileImage;
        ImageViewSquare postImage;
        String userLikes;
        TextView username, timeCreated, caption, likes, comments;
        ImageView solidHeart, hollowHeart, comment;

        UserAccount userAccount = new UserAccount();
        User user  = new User();
        StringBuilder users;
        boolean isLikedByCurUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            //userinfo bar
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.mprofileImage = (CircleImageView) convertView.findViewById(R.id.profileImage);

            //big image
            holder.postImage = (ImageViewSquare) convertView.findViewById(R.id.post_image);

            //like and comment imageview
            holder.solidHeart = (ImageView) convertView.findViewById(R.id.image_heart_solid);
            holder.hollowHeart = (ImageView) convertView.findViewById(R.id.image_heart);
            holder.comment = (ImageView) convertView.findViewById(R.id.image_comment);

            //textview
            holder.likes = (TextView) convertView.findViewById(R.id.image_likes);
            holder.comments = (TextView) convertView.findViewById(R.id.image_comments_link);
            holder.caption = (TextView) convertView.findViewById(R.id.image_caption);
            holder.timeCreated = (TextView) convertView.findViewById(R.id.image_time_posted);

            holder.heart = new Heart(holder.hollowHeart, holder.solidHeart);
            holder.photo = getItem(position);
            holder.detector = new GestureDetector(context, new GestureListener(holder));
            holder.users = new StringBuilder();

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //get the current users username
        getCurUsername();

        //get user likes
        getLikes(holder);

        //set the caption
        holder.caption.setText(getItem(position).getCaption());


        //set the time created
        String timestampDifference = getTimePosted(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeCreated.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeCreated.setText("TODAY");
        }

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImg_path(), holder.mprofileImage);


        //query profile image and username who made the post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_user_account))
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){


                    Log.d(TAG, "onDataChange: user found: "
                            + singleSnapshot.getValue(UserAccount.class).getUsername());

                    //set username
                    holder.username.setText(singleSnapshot.getValue(UserAccount.class).getUsername());
                    //set profileImage
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccount.class).getProfile_image(), holder.mprofileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object
        Query userQuery = dbRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(User.class).getUsername());

                    holder.user = singleSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if(reachedEndOfList(position)){
//            loadMoreData();
//        }

        return convertView;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ViewHolder mHolder;
        public GestureListener(ViewHolder holder){

            mHolder = holder;

        }
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
                    .child(context.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        //photoid
                        String keyID = singleSnapshot.getKey();

                        //check if user already liked the photo
                        if(mHolder.isLikedByCurUser &&
                                singleSnapshot.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            //remove from photos node
                            dbRef.child(context.getString(R.string.dbname_photos))
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            //remove from user_photos node
                            dbRef.child(context.getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //get userid
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikes(mHolder);
                        }
                        //user has not liked the photo
                        else if(!mHolder.isLikedByCurUser){
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void getLikes(final ViewHolder holder){
        Log.d(TAG, "getLikesString: getting likes string");

        try{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(context.getString(R.string.dbname_photos))
                    .child(holder.photo.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.users = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild("user_id")
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    Log.d(TAG, "onDataChange: found like: " +
                                            singleSnapshot.getValue(User.class).getUsername());

                                    holder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                    holder.users.append(",");
                                }

                                String[] splitUsers = holder.users.toString().split(",");

                                if(holder.users.toString().contains(currentUsername + ",")){//mitch, mitchell.tabian
                                    holder.isLikedByCurUser = true;
                                }else{
                                    holder.isLikedByCurUser = false;
                                }

                                int length = splitUsers.length;
                                if(length == 1){
                                    holder.userLikes = "Liked by " + splitUsers[0];
                                }
                                else if(length == 2){
                                    holder.userLikes = "Liked by " + splitUsers[0]
                                            + " and " + splitUsers[1];
                                }
                                else if(length == 3){
                                    holder.userLikes = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + " and " + splitUsers[2];

                                }
                                else if(length > 3){
                                    holder.userLikes = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + " and " + (splitUsers.length - 2) + " others";
                                }
                                Log.d(TAG, "onDataChange: likes string: " + holder.userLikes);
                                //setup likes string
                                setupUserLikesDisplay(holder, holder.userLikes);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        holder.userLikes = "";
                        holder.isLikedByCurUser = false;
                        //setup likes string
                        setupUserLikesDisplay(holder, holder.userLikes);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            holder.userLikes = "";
            holder.isLikedByCurUser = false;
            //setup likes string
            setupUserLikesDisplay(holder, holder.userLikes);
        }
    }

    private void setupUserLikesDisplay(final ViewHolder holder, String userLikes){
        Log.d(TAG, "setupUserLikesDisplay: userlikes:" + holder.userLikes);

        if(holder.isLikedByCurUser){
            Log.d(TAG, "setupUserLikesDisplay: photo is liked by current user");
            holder.hollowHeart.setVisibility(View.GONE);
            holder.solidHeart.setVisibility(View.VISIBLE);
            holder.solidHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }else{
            Log.d(TAG, "setupUserLikesDisplay: photo is not liked by current user");
            holder.hollowHeart.setVisibility(View.VISIBLE);
            holder.solidHeart.setVisibility(View.GONE);
            holder.hollowHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }
        holder.likes.setText(userLikes);
    }

    private void addNewLike(ViewHolder holder){
        Log.d(TAG, "addNewLike: add new like");

        //get new like id
        String newLikeID = dbRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef.child(context.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        dbRef.child(context.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(holder.photo.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikes(holder);
    }

    private String getTimePosted(Photo photo){
        Log.d(TAG, "getTimePosted: getting posted days ago");

        String res = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getData_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            res = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            res = "0";
        }
        return res;
    }

    private void getCurUsername(){
        Log.d(TAG, "getCurUsername: get user account");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_users))
                .orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(UserAccount.class).getUsername();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
