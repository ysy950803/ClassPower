package com.ysy.classpower_utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * Created by 姚圣禹 on 2016/2/17.
 */
public class CardTurnAnimation {

    // SATo: StartAnimationTo

    public ScaleAnimation sato0 = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
    public ScaleAnimation sato1 = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
    private RelativeLayout layoutOne;
    private RelativeLayout layoutTwo;

    public CardTurnAnimation(final RelativeLayout layoutOne, final RelativeLayout layoutTwo) {
        this.layoutOne = layoutOne;
        this.layoutTwo = layoutTwo;
        showWidgetOne();
        sato0.setDuration(250);
        sato1.setDuration(250);

        sato0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (layoutOne.getVisibility() == View.VISIBLE) {
                    layoutOne.setAnimation(null);
                    showWidgetTwo();
                    layoutTwo.startAnimation(sato1);
                } else {
                    layoutTwo.setAnimation(null);
                    showWidgetOne();
                    layoutOne.startAnimation(sato1);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public ScaleAnimation getSato(int number) {
        if (number == 0) {
            return this.sato0;
        } else {
            return this.sato1;
        }
    }

    private void showWidgetOne() {
        layoutOne.setVisibility(View.VISIBLE);
        layoutTwo.setVisibility(View.GONE);
    }

    private void showWidgetTwo() {
        layoutOne.setVisibility(View.GONE);
        layoutTwo.setVisibility(View.VISIBLE);
    }

}
