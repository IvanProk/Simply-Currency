package com.ivan_prokofyev.currencies.support;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.ivan_prokofyev.currencies.MainActivity;

/**
 * Created by Prokofyev Ivan on 05.08.16.
 */
public class CurrencyAnimator {
    int TRANSLATION_DURATION = 400;
    int ALPHA_DURATION = 400;
    private Thread background;

    public CurrencyAnimator() {}

    public void animateCurrencyView(View view, float delta, boolean isMenuShown) {
        int padding = - view.getHeight()/6;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        if (!isMenuShown) {
            if(delta != 0 && view.getY() + delta + padding > 0)
                view.animate().translationY(delta + padding).setDuration(TRANSLATION_DURATION).setInterpolator(interpolator);
        } else {
            view.animate().translationY(0).setDuration(TRANSLATION_DURATION).setInterpolator(interpolator);
        }
    }

    public void changeRatesWithAnimation(final View view, final Runnable block, final Runnable countdown) {
        view.animate().alpha(0).setDuration(ALPHA_DURATION).withStartAction(new Runnable() {
            @Override
            public void run() {
                countdown.run();
            }
        }).withEndAction(new Runnable() {
            @Override
            public void run() {
                block.run();
                view.animate().alpha(1).setDuration(ALPHA_DURATION);
            }
        });
    }


    public void countdownAnimation(final TextView tv, final Double from, final Double to) {
        final Handler mainHandler = new Handler();
        background = new Thread(new Runnable() {
            @Override
            public void run() {
                Double was = from;
                Double delta = to - was;
                Double step = delta / 50;
                Double MIN = 0.0001;

                while (Math.abs(to - was) > MIN) {
                    was += step;
                    final Double finalWas = was;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(String.valueOf(String.format("%1$,.3f", finalWas)));
                        }
                    });
                    try {
                        background.sleep(600 / 50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        background.start();
    }
}
