package com.ivan_prokofyev.currencies.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ivan_prokofyev.currencies.R;
import com.ivan_prokofyev.currencies.support.FontManager;

import java.util.ArrayList;

public class BottomMenu extends LinearLayout implements View.OnClickListener {
    public boolean isShown = false;

    public OnMenuItemClick onMenuItemClick;

    Button activeButton;

    Animation animationOpen = null;
    Animation animationClose = null;
    private LayoutInflater mInflater;
    RelativeLayout.LayoutParams layoutParams;
    FontManager fontManager;



    public interface OnMenuItemClick {
        boolean onItemClick(int id, boolean isActive);
    }

    public BottomMenu(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.menu_layout, this, true);

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);

        fontManager = new FontManager(context);

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add((Button) findViewById(R.id.usd_rub));
        buttons.add((Button) findViewById(R.id.usd_eur));
        buttons.add((Button) findViewById(R.id.eur_rub));
        buttons.add((Button) findViewById(R.id.eur_usd));
        buttons.add((Button) findViewById(R.id.rub_eur));
        buttons.add((Button) findViewById(R.id.rub_usd));

        for (Button btn : buttons) {
            btn.setTypeface(fontManager.typefaceReg);
            btn.setOnClickListener(this);
        }

        activeButton = buttons.get(0);
        activeButton.setTextColor(Color.WHITE);
        activeButton.setTypeface(fontManager.typefaceBold);

        Animation.AnimationListener animListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShown = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        animationOpen = AnimationUtils.loadAnimation(context, R.anim.menu_open);
        animationOpen.setAnimationListener(animListener);
        animationClose = AnimationUtils.loadAnimation(context, R.anim.menu_close);

        setVisibility(INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (isShown)
            if (onMenuItemClick != null && v instanceof Button) {
                if (activeButton != v) {
                    activeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    activeButton = (Button) v;
                    activeButton.setTextColor(Color.WHITE);
                    activeButton.setTypeface(fontManager.typefaceBold);
                    if (onMenuItemClick.onItemClick(v.getId(), false)) {
                        hide();
                    } else if (onMenuItemClick.onItemClick(v.getId(), true))
                        hide();
                }
            }
    }

    public void show() {
        setVisibility(VISIBLE);
        startAnimation(animationOpen);
        setClickable(true);
    }

    public int getMenuHeight(){
        return activeButton.getHeight( ) * 6;
    }

    public void hide() {
        isShown = false;
        setVisibility(INVISIBLE);
        startAnimation(animationClose);
        setClickable(false);
    }

}
