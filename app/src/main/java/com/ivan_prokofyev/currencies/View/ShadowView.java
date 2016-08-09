package com.ivan_prokofyev.currencies.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivan_prokofyev.currencies.R;
import com.ivan_prokofyev.currencies.support.FontManager;

/**
 * Created by Ivan Prokofyev on 30.07.16.
 */
public class ShadowView extends TextView {
    public boolean isShown = false;
    FontManager fontManager;
    RelativeLayout.LayoutParams layoutParams;

    public ShadowView(Context context) {
        super(context);
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fontManager = new FontManager(context);

        setLayoutParams(layoutParams);
        setText(R.string.network_trouble);
        setTypeface(fontManager.typefaceBlack);
        setTextSize(20);
        setPadding(10, 10, 10, 10);
        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.WHITE);
        setY(-((Activity) context).getWindowManager().getDefaultDisplay().getHeight());//HOTFIX
        setVisibility(INVISIBLE);
    }

    public void show(boolean haveSaved) {
        if (haveSaved) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                setText(Html.fromHtml(getContext().getString(R.string.cant_update)));
            else
                setText(Html.fromHtml(getContext().getString(R.string.cant_update_api_16)));
        } else {
            setText(Html.fromHtml(getContext().getString(R.string.network_trouble)));
        }
        animate().y(0).setInterpolator(new DecelerateInterpolator()).setDuration(500);
        setVisibility(VISIBLE);
        isShown = true;
    }

    public void hide(final Runnable withEndAction) {
        isShown = false;
        this.animate().translationY(-getHeight()).setInterpolator(new AccelerateInterpolator()).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (withEndAction != null)
                    withEndAction.run();
                setVisibility(INVISIBLE);
            }
        });
    }
}
