package com.nailiqi.travellingpaws.Share;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.FilePathMethods;
import com.nailiqi.travellingpaws.Utils.ImageGridViewAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment{
    private static final String TAG = "LibraryFragment";
    private static int Grid_COL_NUM = 4;
    private static String append = "file:/";

    private GridView gridView;
    private ImageView previewImage;
    private Spinner spinner;

    private List<String> fileDirs;
    private FilePathMethods filePathMethods;
    private String selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);
        previewImage = (ImageView) view.findViewById(R.id.libraryImageView);
        spinner = (Spinner) view.findViewById(R.id.spinnerShare);
        fileDirs = new ArrayList<>();
        filePathMethods = new FilePathMethods();

        //cancel text view
        TextView cancelShare = (TextView) view.findViewById(R.id.tvCancelShare);
        cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cancel library fragment.");
                getActivity().finish();
            }
        });

        //next textview
        TextView nextShare = (TextView) view.findViewById(R.id.tvNextShare);
        nextShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to next share.");

                Intent intent = new Intent(getActivity(), NextActivity.class);
                intent.putExtra(getString(R.string.selected_image), selectedImage);
                startActivity(intent);

            }
        });

        setupWidgets();

        return view;
    }

    private void setupWidgets(){

        //check folders indide "/storage/emulated/0/pictures"
        if(FilePathMethods.getDirectoryPaths(filePathMethods.PICTURES) != null){
            fileDirs = FilePathMethods.getDirectoryPaths(filePathMethods.PICTURES);
        }

        fileDirs.add(filePathMethods.CAMERA);

        //setup spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, fileDirs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + fileDirs.get(position));

                //setup grid view
                setupGridview(fileDirs.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupGridview(String selectedDirectory){
        final List<String> imgUrls = FilePathMethods.getFilePaths(selectedDirectory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/Grid_COL_NUM;
        gridView.setColumnWidth(imageWidth);

        //use grid adapter
        ImageGridViewAdapter adapter = new ImageGridViewAdapter(getActivity(), R.layout.section_grid_imageview, append, imgUrls);
        gridView.setAdapter(adapter);

        //set the default image to show when the fragment is inflated
        try{
            setImage(imgUrls.get(0), previewImage, append);
            selectedImage = imgUrls.get(0);
        }catch (Exception e){
            Log.e(TAG, "setupGridView: Exception: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgUrls.get(position));

                setImage(imgUrls.get(position), previewImage, append);
                selectedImage = imgUrls.get(position);
            }
        });
    }

    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
}
