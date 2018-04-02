package com.nailiqi.travellingpaws.Profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nailiqi.travellingpaws.Utils.ImageViewSquare;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import com.nailiqi.travellingpaws.R;

import java.util.List;

public class ImageGridViewAdapter extends ArrayAdapter<String>{
    private Context context;
    private LayoutInflater layoutInflater;
    private int layoutSrc;
    private String append;
    private List<String> imgUrls;

    public ImageGridViewAdapter(Context context, int layoutSrc, String append, List<String> imgUrls) {
        super(context, layoutSrc,imgUrls);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.layoutSrc = layoutSrc;
        this.append = append;
        this.imgUrls = imgUrls;
    }

    private static class ViewHolder{
        ImageViewSquare gridImageView;
        ProgressBar gridImageProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern
         */
        final ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(layoutSrc, parent, false);
            holder = new ViewHolder();
            holder.gridImageProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressBar);
            holder.gridImageView = (ImageViewSquare) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, holder.gridImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(holder.gridImageProgressBar != null){
                    holder.gridImageProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(holder.gridImageProgressBar != null){
                    holder.gridImageProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(holder.gridImageProgressBar != null){
                    holder.gridImageProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(holder.gridImageProgressBar != null){
                    holder.gridImageProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

}
