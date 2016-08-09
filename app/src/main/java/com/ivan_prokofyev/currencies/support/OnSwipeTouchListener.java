package com.ivan_prokofyev.currencies.support;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Prokofyev Ivan on 31.07.16.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private static final String TAG = "MY_TAG";

    public enum SWIPE_DIRECTION {UNDEFINED, SWIPE_UP, SWIPE_DOWN}

    private final GestureDetector gestureDetector;
    public OnSwipeListener listener;

    public interface OnSwipeListener {
        void onSwipeDetect(SWIPE_DIRECTION direction);
        void onSingleTap();
    }

    public OnSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        listener.onSwipeDetect(SWIPE_DIRECTION.SWIPE_DOWN);
                    } else {
                        listener.onSwipeDetect(SWIPE_DIRECTION.SWIPE_UP);
                    }
                }
                result = true;
            } catch (Exception exception) {
                Log.e(TAG, "onFling Error, message is: " + exception.getMessage());
            }
            return result;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            listener.onSingleTap();
            return super.onSingleTapConfirmed(e);
        }
    }
}


