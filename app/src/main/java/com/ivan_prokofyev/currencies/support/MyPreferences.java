package com.ivan_prokofyev.currencies.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ivan_prokofyev.currencies.server.Currency;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by Prokofyev Ivan on 09.07.2016.
 */
public class MyPreferences {
    private static final String TAG = "MY_TAG";
    private static MyPreferences ourInstance;
    private SharedPreferences myPrefs;
    private static final String PREFS_NAME = "CurrenciesPrefs";
    private final String LATEST = "latest";
    private final String YESTER = "yester";


    private MyPreferences() {
    }
    public static MyPreferences getInstance(Context context) {
        if (ourInstance == null)
            synchronized (MyPreferences.class) {
                if (ourInstance == null) {
                    ourInstance = new MyPreferences();
                    ourInstance.myPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                }
            }
        return ourInstance;
    }

    public void saveLatestRate(Currency latest) {
        try {
            myPrefs.edit().putString(LATEST, new Gson().toJson(latest)).apply();
            Log.d(TAG, "saveLatestRate: Latest rates successfully saved");
        } catch (Exception e) {
            Log.d(TAG, "saveRates: Cant save Latest rates. Cause is: " + e.getMessage());
        }
    }

    public void saveYesterRate(Currency yester) {
        try {
            myPrefs.edit().putString(YESTER, new Gson().toJson(yester)).apply();
            Log.d(TAG, "saveYesterRate: Yesterday rates successfully saved");
        } catch (Exception e) {
            Log.d(TAG, "saveRates: Cant save yester rates. Cause is: " + e.getMessage());
        }

    }

    public Currency readLatestRates() {
        try {
            Currency latestRates = new Gson().fromJson(myPrefs.getString(LATEST, " "), Currency.class);
            Log.d(TAG, "readLatestRates: " + myPrefs.getString(LATEST, " "));

            return latestRates;
        } catch (Exception e) {
            Log.d("MY_TAG", "MyPreferences: readLatestRates: " + String.valueOf(e) + " ===myPrefs:" + myPrefs.getString("rates", " "));
            return null;
        }
    }

    public Currency readYesterRates() {
        try {
            Currency yesterRates = new Gson().fromJson(myPrefs.getString(YESTER, " "), Currency.class);
            Log.d(TAG, "readYesterRates: " + myPrefs.getString(YESTER, " "));
            return yesterRates;
        } catch (Exception e) {
            return null;
        }

    }
}