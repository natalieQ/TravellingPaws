package com.nailiqi.travellingpaws.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.models.User;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nailiqi.travellingpaws.models.UserCombine;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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

}
