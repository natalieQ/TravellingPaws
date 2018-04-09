package com.nailiqi.travellingpaws.Share;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nailiqi.travellingpaws.R;

import static com.nailiqi.travellingpaws.Utils.Permissions.CAMERA_PERMISSION;

public class PhotoFragment extends Fragment{
    private static final String TAG = "PhotoFragment";
    private static final int CAMERA_REQUEST_CODE = 7;
    private static final int PHOTO_TAB = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Button btnOpenCamera = (Button) view.findViewById(R.id.btn_openCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ShareActivity)getActivity()).getCurTabNum() == PHOTO_TAB){  //photo tab is clicked
                    if(((ShareActivity)getActivity()).checkPermissions(CAMERA_PERMISSION)){

                        //start camera
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        //clear activity stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE ){
            Log.d(TAG, "onActivityResult: a photo is taken");
        }
    }


}
