package com.ivan_prokofyev.currencies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import com.ivan_prokofyev.currencies.View.BottomMenu;
import com.ivan_prokofyev.currencies.View.CurrencyView;
import com.ivan_prokofyev.currencies.View.ShadowView;
import com.ivan_prokofyev.currencies.support.DateSupport;
import com.ivan_prokofyev.currencies.support.FontManager;
import com.ivan_prokofyev.currencies.support.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MY_TAG";
    private int tries = 0;

    private ImageButton btnMenu;
    private TextView tvStatus;
    private ProgressBar progressBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout main;
    private CurrencyService.CURRENCY_TYPE current_type = CurrencyService.CURRENCY_TYPE.USD_RUB;
    private ShadowView shadowView;
    private Date lastUpdate;
    private CurrencyView currencyView;
    private BottomMenu bottomMenu;
    private OnSwipeTouchListener onSwipeListener;
    private FontManager fontManager;
    private LayoutInflater mInflater;
    private boolean noProgressBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        showRates(current_type, false);
    }

    private void init() {
        main = (RelativeLayout) findViewById(R.id.rl_main);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            progressBar = (ProgressBar) mInflater.inflate(R.layout.progress_bar, main, false);
            main.addView(progressBar);
        } catch (InflateException e) {
            noProgressBar = true;
            e.printStackTrace();
        }

        fontManager = new FontManager(this);
        currencyView = new CurrencyView(this);
        shadowView = new ShadowView(MainActivity.this);
        bottomMenu = new BottomMenu(this);
        onSwipeListener = new OnSwipeTouchListener(this);

        tvStatus.setTypeface(fontManager.typefaceBlack);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (!noProgressBar)
            progressBar.setVisibility(View.INVISIBLE);
        bottomMenu.setOnTouchListener(onSwipeListener);
        main.setOnTouchListener(onSwipeListener);
        main.setClickable(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorAccent,
                R.color.colorPrimaryLight,
                R.color.colorAccent2);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMenu.setClickable(false);
                prepareLayoutForBottomMenu(true);
            }
        });
        bottomMenu.onMenuItemClick = new BottomMenu.OnMenuItemClick() {
            @Override
            public boolean onItemClick(int id, boolean isActive) {
                if (isActive) {
                    return false;
                } else {
                    onMenuItemClick(id);
                    return true;
                }
            }
        };
        onSwipeListener.listener = new OnSwipeTouchListener.OnSwipeListener() {
            @Override
            public void onSwipeDetect(OnSwipeTouchListener.SWIPE_DIRECTION direction) {
                if (direction == OnSwipeTouchListener.SWIPE_DIRECTION.SWIPE_DOWN) {
                    if (bottomMenu.isShown)
                        prepareLayoutForBottomMenu(false);
                } else if (direction == OnSwipeTouchListener.SWIPE_DIRECTION.SWIPE_UP) {
                    if (currencyView.getLastRate() != 0.0)
                        if (shadowView.isShown) {
                            prepareLayoutForShadowView(false);
                            showRates(current_type, false);
                        } else if (!bottomMenu.isShown) {
                            prepareLayoutForBottomMenu(true);
                            bottomMenu.setClickable(true);//to BottomMenu
                        }
                }
            }

            @Override
            public void onSingleTap() {
                if (bottomMenu.isShown)
                    prepareLayoutForBottomMenu(false);
            }
        };

        main.addView(currencyView, currencyView.getLayoutParams());
        addContentView(bottomMenu, bottomMenu.getLayoutParams());
        main.addView(shadowView, shadowView.getLayoutParams());
    }

    private void showRates(final CurrencyService.CURRENCY_TYPE type, final boolean withUpdate) {
        tvStatus.setText("");
        if (!noProgressBar)
            progressBar.setVisibility(View.VISIBLE);
        if (tries < 10)
            CurrencyService.getInstance(this).getRatesWithDelta(type, withUpdate, new CurrencyService.ReturnBlock() {
                @Override
                public void show(Double result, Double delta, final Date date, Throwable t) {
                    if (t == null && result != null) {
                        currencyView.showRate(result, delta, date, t, new CurrencyView.ShowFeatures() {
                            @Override
                            public void run(boolean successful) {
                                showStatus(date);
                                if (successful) {
                                    if (!noProgressBar)
                                        progressBar.setVisibility(View.INVISIBLE);
                                    if (shadowView.isShown) {
                                        prepareLayoutForShadowView(false);
                                    }
                                } else {
                                    tries++;
                                    MainActivity.this.showRates(current_type, true); // try again
                                }
                            }
                        });
                    } else {
                        prepareLayoutForShadowView(true);
                    }
                    if (withUpdate)
                        mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        else
            Toast.makeText(this, getString(R.string.network_trouble), Toast.LENGTH_SHORT).show();
    }

    private void showStatus(Date date) {
        lastUpdate = date;
        if (DateSupport.format(date).equals(DateSupport.getCurrentDate())) {
            tvStatus.setText(getString(R.string.today_status) + DateSupport.formatTime(date));
        } else if (DateSupport.format(date).equals(DateSupport.getYesterdayDate())) {
            tvStatus.setText(getString(R.string.yesterday_status) + DateSupport.formatTime(date));
        } else {
            tvStatus.setText(getString(R.string.status) + DateSupport.formatToHumanic(date) + getString(R.string.at) + DateSupport.formatTime(date));
        }
    }

    private void prepareLayoutForBottomMenu(boolean toShowBottomMenu) {
        float delta = 0;

        if (toShowBottomMenu) {
            if (currencyView.getHeight() + currencyView.getY() + bottomMenu.getMenuHeight() > main.getHeight())
                delta = main.getHeight() - (currencyView.getHeight() + currencyView.getY() + bottomMenu.getMenuHeight());
            bottomMenu.show();
            currencyView.moveCurrencyView(delta, false);
            btnMenu.setClickable(false);
        } else {
            bottomMenu.hide();
            currencyView.moveCurrencyView(0, true);
            btnMenu.setClickable(true);
        }
    }

    private void prepareLayoutForShadowView(boolean toShowShadowView) {
        if (toShowShadowView) {
            if (shadowView.isShown)
                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
            else if (currencyView.getLastRate() != 0.0) {
                currencyView.hide();
                shadowView.show(true);
            } else {
                currencyView.hide();
                shadowView.show(false);
            }
        } else {
            shadowView.hide(new Runnable() {
                @Override
                public void run() {
                    currencyView.show();
                    showStatus(lastUpdate);
                    if (!noProgressBar)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void onMenuItemClick(int id) {
        switch (id) {
            case R.id.usd_rub:
            default:
                currencyView.setCurrentCurrency(R.string.usd_rub);
                currencyView.setCurrentValutes(getString(R.string.usd), getString(R.string.rub));
                current_type = CurrencyService.CURRENCY_TYPE.USD_RUB;
                break;

            case R.id.usd_eur:
                currencyView.setCurrentCurrency(R.string.usd_eur);
                currencyView.setCurrentValutes(getString(R.string.usd), getString(R.string.eur));
                current_type = CurrencyService.CURRENCY_TYPE.USD_EUR;
                break;

            case R.id.eur_rub:
                currencyView.setCurrentCurrency(R.string.eur_rub);
                currencyView.setCurrentValutes(getString(R.string.eur), getString(R.string.rub));
                current_type = CurrencyService.CURRENCY_TYPE.EUR_RUB;
                break;

            case R.id.eur_usd:
                currencyView.setCurrentCurrency(R.string.eur_usd);
                currencyView.setCurrentValutes(getString(R.string.eur), getString(R.string.usd));
                current_type = CurrencyService.CURRENCY_TYPE.EUR_USD;
                break;

            case R.id.rub_eur:
                currencyView.setCurrentCurrency(R.string.rub_eur);
                currencyView.setCurrentValutes(getString(R.string.rub), getString(R.string.eur));
                current_type = CurrencyService.CURRENCY_TYPE.RUB_EUR;
                break;

            case R.id.rub_usd:
                currencyView.setCurrentCurrency(R.string.rub_usd);
                currencyView.setCurrentValutes(getString(R.string.rub), getString(R.string.usd));
                current_type = CurrencyService.CURRENCY_TYPE.RUB_USD;
                break;
        }

        showRates(current_type, false);
        prepareLayoutForBottomMenu(false);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (!bottomMenu.isShown)
                    prepareLayoutForBottomMenu(true);
                break;
            case KeyEvent.KEYCODE_BACK:
                if (bottomMenu.isShown) {
                    prepareLayoutForBottomMenu(false);
                    return true;
                } else if (shadowView.isShown && currencyView.getLastRate() != 0.0) {
                    prepareLayoutForShadowView(false);
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run Refresh started !");
                showRates(current_type, true);

            }
        });

    }


}
