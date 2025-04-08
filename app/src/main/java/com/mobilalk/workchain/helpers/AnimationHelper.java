package com.mobilalk.workchain.helpers;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

public class AnimationHelper {
    public static void delayAnimation(final View view, int delay, Context context, int animationId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(AnimationUtils.loadAnimation(context, animationId));
                view.setVisibility(View.VISIBLE);
            }
        }, delay);
    }
}
