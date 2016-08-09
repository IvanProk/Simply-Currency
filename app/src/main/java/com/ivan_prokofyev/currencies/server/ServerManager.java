package com.ivan_prokofyev.currencies.server;

import android.util.Log;

import com.ivan_prokofyev.currencies.CurrencyService;
import com.ivan_prokofyev.currencies.support.DateSupport;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Prokofyev Ivan on 06.07.2016.
 */
public class ServerManager {
    private static ServerManager instance;
    private ApiFixer apiFixer;
    private static String BASE_URL = "http://www.apilayer.net";
    private static String API_KEY = "9658a96a432f7644ddb416b0e09ed6dc";
    private static String CURRENCIES = "EUR,RUB";
    private static int FORMAT = 1;


    private ServerManager() {
    }

    public static ServerManager getInstance() {
        if (instance == null)
            synchronized (ServerManager.class) {
                if (instance == null) {
                    instance = new ServerManager();
                    instance.apiFixer = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiFixer.class);
                }
            }
        return instance;
    }

    public interface ResponseBlock<T> {
        void blockResponse(Currency response, Throwable t, CurrencyService.CURRENCY_DATE date);
    }

    public void downloadCurrency(final CurrencyService.CURRENCY_DATE date, final ResponseBlock<Currency> block) {

        Callback<Currency> callback = new Callback<Currency>() {
            @Override
            public void onResponse(Call<Currency> call, Response<Currency> response) {
                if (response.isSuccessful()) {
                    response.body().updateTime = Calendar.getInstance().getTime();
                    block.blockResponse(response.body(), null, date);
                } else {
                    block.blockResponse(null, new Throwable(String.valueOf(response.code())), date);
                }
            }
            @Override
            public void onFailure(Call<Currency> call, Throwable t) {
                block.blockResponse(null, t, date);
            }
        };

        if (date == CurrencyService.CURRENCY_DATE.LATEST) {
            apiFixer.getYesterRates(API_KEY, DateSupport.getCurrentDateGMT(), CURRENCIES, FORMAT).enqueue(callback);
        } else if (date == CurrencyService.CURRENCY_DATE.YESTERDAY) {
            apiFixer.getYesterRates(API_KEY, DateSupport.getYesterdayDateGMT()
                    ,CURRENCIES, FORMAT).enqueue(callback);
        }
    }
}

