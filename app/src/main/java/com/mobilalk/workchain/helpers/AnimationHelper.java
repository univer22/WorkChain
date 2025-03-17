package com.mobilalk.workchain.helpers;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.mobilalk.workchain.R;

public class AnimationHelper {
    public static void delayAnimation(final View view, int delay, Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.input_animation));
                view.setVisibility(View.VISIBLE);
            }
        }, delay);
    }
}
