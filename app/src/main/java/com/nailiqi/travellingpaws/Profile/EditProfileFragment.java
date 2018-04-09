package com.nailiqi.travellingpaws.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.FirebaseMethods;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;
import com.nailiqi.travellingpaws.models.User;
import com.nailiqi.travellingpaws.models.UserAccount;
import com.nailiqi.travellingpaws.models.UserCombine;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment{

    private static final String TAG = "EditProfileFragment";
    private CircleImageView profileImage;
    private EditText username;
    private EditText petname;
    private EditText description;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private FirebaseMethods firebaseMethods;
    private String userID;
    private UserCombine mUserCombine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        profileImage = (CircleImageView) view.findViewById(R.id.profilePic);
        username = (EditText) view.findViewById(R.id.username);
        petname = (EditText) view.findViewById(R.id.petname);
        description = (EditText) view.findViewById(R.id.description);

        firebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();

        //go back to "ProfileActivity"
        ImageView goback = (ImageView) view.findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go back to ProfileActivity");
                getActivity().finish();
            }
        });

        //setup update buttom
        ImageView savemark = (ImageView) view.findViewById(R.id.save);
        savemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                updateProfile();
            }
        });

        return view;
    }

    //hard-code version
//    private void setProfileImage(){
//        String imgUrl = "http://www.worldclassbichons.com/uploads/3/4/6/0/34608590/edited/img-2647_1.jpeg";
//        ImageLoaderHelper.setImage(imgUrl, profileImage,null, "");
//    }

    /**
     * setup widgets
     */
    private void setupWidgets(UserCombine userCombine){

        mUserCombine = userCombine;
        User user = userCombine.getUser();
        UserAccount account = userCombine.getUserAccount();

        //set profile image
        ImageLoaderHelper.setImage(account.getProfile_image(), profileImage, null, "");

        //set profile textview
        username.setText(account.getUsername());
        description.setText(account.getDescription());
        petname.setText(account.getPetname());
    }

    /**
     * update profile info and save to database
     */
    private void updateProfile(){
        final String newUsername = username.getText().toString();
        String newPetname = petname.getText().toString();
        String newDescription = description.getText().toString();

        //listener to single change
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: cur username is: " + mUserCombine.getUser().getUsername());
                if(!mUserCombine.getUser().getUsername().equals(newUsername)){ //user change the username
                    isUsernameExist(newUsername);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isUsernameExist(final String newUsername) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))   //users node
                .orderByChild("username")
                .equalTo(newUsername);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){  //username does not exist

                    firebaseMethods.updateUsername(newUsername);
                    Toast.makeText(getActivity(), "Saved new username", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.exists()){
                        Toast.makeText(getActivity(), "User already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        userID = mAuth.getCurrentUser().getUid();

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
