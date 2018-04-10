package com.nailiqi.travellingpaws.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {
    private static final String TAG = "Heart";

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView hollowHeart, solidHeart;

    public Heart(ImageView hollowHeart, ImageView solidHeart) {
        this.hollowHeart = hollowHeart;
        this.solidHeart = solidHeart;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: toggling heart.");

        //add animation
        AnimatorSet animationSet =  new AnimatorSet();

        //check visibility
        if(solidHeart.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling solid heart off.");
            solidHeart.setScaleX(0.1f);
            solidHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(solidHeart, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(solidHeart, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            solidHeart.setVisibility(View.GONE);
            hollowHeart.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        else if(solidHeart.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling solid heart on.");
            solidHeart.setScaleX(0.1f);
            solidHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(solidHeart, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(solidHeart, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERATE_INTERPOLATOR);

            solidHeart.setVisibility(View.VISIBLE);
            hollowHeart.setVisibility(View.GONE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        animationSet.start();

    }
}
