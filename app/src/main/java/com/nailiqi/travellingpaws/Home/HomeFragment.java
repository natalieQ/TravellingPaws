package com.nailiqi.travellingpaws.Home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.MainListAdapter;
import com.nailiqi.travellingpaws.models.Like;
import com.nailiqi.travellingpaws.models.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";

    private List<Photo> photoList;
    private ListView listView;
    private MainListAdapter adapter;
    private List<Photo> paginatedPhotoList;
    private int feedCount;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        photoList = new ArrayList<>();

        getPhotos();

        return view;
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_photos));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get("caption").toString());
                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                    photo.setUser_id(objectMap.get("user_id").toString());
                    photo.setData_created(objectMap.get("data_created").toString());
                    photo.setImg_path(objectMap.get("img_path").toString());
                    photo.setGps_longitude(Double.parseDouble(objectMap.get("gps_longitude").toString()));
                    photo.setGps_longitude(Double.parseDouble(objectMap.get("gps_latitude").toString()));


                    photoList.add(photo);
                }

                displayPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayPhotos(){
        paginatedPhotoList = new ArrayList<>();
        if(photoList != null){
            try{

                //sort by date created
                Collections.sort(photoList, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getData_created().compareTo(o1.getData_created());
                    }
                });

                int iterations = photoList.size();

                if(iterations > 10){
                    iterations = 10;
                }

                feedCount = 10;
                for(int i = 0; i < iterations; i++){
                    paginatedPhotoList.add(photoList.get(i));
                }

                adapter = new MainListAdapter(getActivity(), R.layout.layout_main, paginatedPhotoList);
                listView.setAdapter(adapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(photoList.size() > feedCount && photoList.size() > 0){

                int iterations;
                if(photoList.size() > (feedCount + 10)){
                    Log.d(TAG, "displayMorePhotos: > 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: < 10 more photos");
                    iterations = photoList.size() - feedCount;
                }

                //add photos to the paginated list
                for(int i = feedCount; i < feedCount + iterations; i++){
                    paginatedPhotoList.add(photoList.get(i));
                }
                feedCount = feedCount + iterations;
                adapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}
