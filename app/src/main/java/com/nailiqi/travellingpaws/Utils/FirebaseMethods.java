package com.nailiqi.travellingpaws.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.models.Photo;
import com.nailiqi.travellingpaws.models.User;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nailiqi.travellingpaws.models.UserCombine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;


    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Register new user
     */
    public void registerNewEmail(String email, String password, String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }

    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");

        User user = new User();

        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());

            if(user.getUsername().equals(username)){
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
                return true;
            }
        }
        return false;
    }

    public void addNewUser(String email, String username, String description, String profile_image, String petname){

        User user = new User( userID, email, username );

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccount userAccount = new UserAccount(
                description,
                0,
                0,
                0,
                profile_image,
                username,
                petname
        );

        myRef.child(mContext.getString(R.string.dbname_user_account))
                .child(userID)
                .setValue(userAccount);

    }


    public UserCombine getUserCombine(DataSnapshot dataSnapshot){

        Log.d(TAG, "getUserAccount: getting user_acount info from database");

        User user = new User();
        UserAccount account = new UserAccount();


        for(DataSnapshot ds : dataSnapshot.getChildren()){

            //get user_account info from database
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account))){
                Log.d(TAG, "getUserAccount: datasnapshop: " + ds);

                try{
                    account.setDescription(ds.child(userID).getValue(UserAccount.class).getDescription());
                    account.setFollowers(ds.child(userID).getValue(UserAccount.class).getFollowers());
                    account.setFollowing(ds.child(userID).getValue(UserAccount.class).getFollowing());
                    account.setPosts(ds.child(userID).getValue(UserAccount.class).getPosts());
                    account.setProfile_image(ds.child(userID).getValue(UserAccount.class).getProfile_image());
                    account.setUsername(ds.child(userID).getValue(UserAccount.class).getUsername());
                    account.setPetname(ds.child(userID).getValue(UserAccount.class).getPetname());

                }catch (NullPointerException ex){
                    Log.e(TAG, "getUserAccount: NullPointerException " + ex.getMessage() );
                }
                Log.d(TAG, "getUserAccount: get user_account info: " + account.toString());
            }

            //get users info from database
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserAccount: datasnapshop: " + ds);

                try{
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                    user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                    user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());


                }catch (NullPointerException ex){
                    Log.e(TAG, "getUserAccount: NullPointerException " + ex.getMessage() );
                }
                Log.d(TAG, "getUserAccount: get user info: " + user.toString());
            }
        }

        return new UserCombine(user,account);

    }

    public void updateUsername(String username){
        //udpate users
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child("username")
                .setValue(username);
        //udpate user_account
        myRef.child(mContext.getString(R.string.dbname_user_account))
                .child(userID)
                .child("username")
                .setValue(username);
    }

    public void updateUserAccount(String petname, String description){

        Log.d(TAG, "updateUserAccount: updating user account info");

        //udpate petname
        if(petname != null){
            myRef.child(mContext.getString(R.string.dbname_user_account))
                    .child(userID)
                    .child("petname")
                    .setValue(petname);
        }

        //udpate description
        if(description != null){
            myRef.child(mContext.getString(R.string.dbname_user_account))
                    .child(userID)
                    .child("description")
                    .setValue(description);
        }

        
    }

    public int getImageCount(DataSnapshot dataSnapshot) {

        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;

    }

    public void uploadNewPhoto (String photoType, final String caption, int imgCount, String imgUrl){

        FilePathMethods filePaths = new FilePathMethods();
        //add new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_STORAGE_PHOTOS + "/" + user_id + "/photo" + (imgCount + 1));

            //convert image url to bitmap
            Bitmap bitmap = ImageHelper.toBitmap(imgUrl);
            //convert bitmap to byte, default quality set to 100
            byte[] bytes = ImageHelper.toBytes(bitmap, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //photo url on firebase
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add new photo to 'photos' node and 'user_photos' node
                    uploadPhotoToDatabase(caption, firebaseUrl.toString());

                    //navigate to home activity

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 10 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }
        //add new profile photo - not implemented yet......
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new profile photo");
        }

    }

    private void uploadPhotoToDatabase(String caption, String url){
        Log.d(TAG, "addPhotoToDatabase: upload photo to firebase database.");

        String newPhotoID = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setData_created(getTimestamp());
        photo.setImg_path(url);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoID);

        //set gps -- to be implemented

        //add to user_photos node
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoID).setValue(photo);
        //add to photos node
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoID).setValue(photo);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return sdf.format(new Date());
    }


}
