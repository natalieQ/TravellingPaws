package com.nailiqi.travellingpaws.Share;


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

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment{
    private static final String TAG = "LibraryFragment";

    private GridView gridView;
    private ImageView selectedImage;
    private Spinner spinner;

    private List<String> fileDirs;
    private FilePathMethods filePathMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);
        selectedImage = (ImageView) view.findViewById(R.id.libraryImageView);
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

            }
        });

        setupWidgets();

        return view;
    }

    private void setupWidgets(){

        //check folders indide "/storage/emulated/0/pictures"
        if(filePathMethods.getDirectoryPaths(filePathMethods.PICTURES) != null){
            fileDirs = filePathMethods.getDirectoryPaths(filePathMethods.PICTURES);
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

                //setup our image grid for the directory chosen
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
