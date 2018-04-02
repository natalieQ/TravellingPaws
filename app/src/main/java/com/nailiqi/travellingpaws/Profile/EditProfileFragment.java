package com.nailiqi.travellingpaws.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditProfileFragment extends Fragment{

    private static final String TAG = "EditProfileFragment";
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        profileImage = (ImageView) view.findViewById(R.id.profilePic);

        setProfileImage();

        //go back to "ProfileActivity"
        ImageView goback = (ImageView) view.findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go back to ProfileActivity");
                getActivity().finish();
            }
        });

        return view;
    }


    private void setProfileImage(){
        String imgUrl = "http://www.worldclassbichons.com/uploads/3/4/6/0/34608590/edited/img-2647_1.jpeg";
        ImageLoaderHelper.setImage(imgUrl, profileImage,null, "");
    }

}
