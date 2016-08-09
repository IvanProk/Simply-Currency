package com.ivan_prokofyev.currencies.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivan_prokofyev.currencies.MainActivity;
import com.ivan_prokofyev.currencies.R;
import com.ivan_prokofyev.currencies.support.CurrencyAnimator;
import com.ivan_prokofyev.currencies.support.FontManager;

import java.util.Date;

/**
 * Created by Prokofyev Ivan on 15.07.16.
 */
public class CurrencyView extends RelativeLayout {
    private TextView tvCurrency;
    private TextView tvRate;
    private TextView tvInfo;

    private String valuta;
    private String valuta2;
    private String trend = "";
    private int currentCurrency = R.string.usd_rub;
    private Double lastRate = 0.0;
    public boolean isShown;

    private CurrencyAnimator animator;
    private Context context;
    private LayoutInflater mInflater;
    private RelativeLayout.LayoutParams layoutParams;
    private WindowManager windowManager;
    private FontManager fontManager;

    public interface ShowFeatures {
        void run(boolean successful);
    }

    public CurrencyView(Context context) {
        super(context);
        init(context);
    }

    public CurrencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CurrencyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.currency_view, this, true);

        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fontManager = new FontManager(context);
        animator = new CurrencyAnimator();

        tvCurrency = (TextView) findViewById(R.id.tvCurrency);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvRate = (TextView) findViewById(R.id.tvRate);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        tvRate.setTypeface(fontManager.typefaceReg);
        tvCurrency.setTypeface(fontManager.typefaceBold);
        tvInfo.setTypeface(fontManager.typefaceMI);
        layoutParams.setMargins(0, windowManager.getDefaultDisplay().getHeight() / 6, 0, 0);
        this.setLayoutParams(layoutParams);
        valuta = context.getString(R.string.usd);
        valuta2 = context.getString(R.string.rub);

        isShown = true;
    }

    public void showRate(final Double result, final Double delta, final Date date, final Throwable t, final ShowFeatures features) {
        animator.changeRatesWithAnimation(this, new Runnable() {
            @Override
            public void run() {
                setStrings(result, delta, t, features);
            }
        }, new Runnable() {
            @Override
            public void run() {
                animator.countdownAnimation(tvRate, lastRate, result);
            }
        });
    }

    private void setStrings(Double result, Double delta, Throwable t, ShowFeatures features) {
        try {
            boolean isPreserved = (Math.abs(delta) < 0.01);
            if (t != null || result == null)
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            else {
                tvCurrency.setText(currentCurrency);

                if (valuta2.equals(context.getString(R.string.rub)))                                //Setting current currencies
                    valuta2 = context.getString(R.string.rub2);
                else if (valuta2.equals(context.getString(R.string.usd)))
                    valuta2 = context.getString(R.string.usd2);
                else if (valuta2.equals(context.getString(R.string.eur)))
                    valuta2 = context.getString(R.string.eur2);

                if (delta > 0) {                                                                    //determining currency behavior trend
                    trend = context.getString(R.string.up);
                    tvInfo.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if (delta < 0) {
                    delta *= -1;
                    trend = context.getString(R.string.fell);
                    tvInfo.setTextColor(getResources().getColor(R.color.colorAccent2));
                }
                if (isPreserved) {
                    trend = context.getString(R.string.preserved);                                  //Setting text in dependence of trend
                    tvInfo.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvInfo.setText(context.getString(R.string.info) + trend);
                } else
                    tvInfo.setText(context.getString(R.string.info) + valuta + trend + valuta2 + context.getString(R.string.by) + String.valueOf(String.format("%1$.2f", delta) + context.getString(R.string.perc)));

                lastRate = result;
                features.run(true);
            }
        } catch (Exception e) {
            features.run(false);
            Log.e(MainActivity.TAG, "showRate: Error!" + " Message is: " + e.getMessage());
        }
    }

    public Double getLastRate() {
        return lastRate;
    }

    public void setCurrentCurrency(int currency) {
        this.currentCurrency = currency;
    }

    public void setCurrentValutes(String valuta1, String valuta2) {
        this.valuta = valuta1;
        this.valuta2 = valuta2;
    }

    public void moveCurrencyView(float deltaY, boolean isMenuShown) {
        animator.animateCurrencyView(this, deltaY, isMenuShown);
    }

    public void show() {
        setVisibility(VISIBLE);
        isShown = true;
    }

    public void hide() {
        setVisibility(INVISIBLE);
        isShown = false;
    }

}