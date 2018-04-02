package com.nailiqi.travellingpaws.Utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ImageViewSquare extends AppCompatImageView{

    public ImageViewSquare(Context context) {
        super(context);
    }

    public ImageViewSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewSquare(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
