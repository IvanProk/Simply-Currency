package com.ivan_prokofyev.currencies;

import android.content.Context;
import android.util.Log;

import com.ivan_prokofyev.currencies.server.Currency;
import com.ivan_prokofyev.currencies.server.ServerManager;
import com.ivan_prokofyev.currencies.server.ServerManager.ResponseBlock;
import com.ivan_prokofyev.currencies.support.DateSupport;
import com.ivan_prokofyev.currencies.support.MyPreferences;

import java.util.Date;

/**
 * Created by Prokofyev Ivan on 09.07.2016.
 */

public class CurrencyService {


    public enum CURRENCY_DATE {ALL, YESTERDAY, LATEST}
    public enum CURRENCY_TYPE {UNDEFINED, USD_RUB, RUB_USD, EUR_RUB, RUB_EUR, EUR_USD, USD_EUR}

    private static MyPreferences mPrefs;
    private static Currency latestRates, yesterRates;
    private CurrencyService() {
    }
    private static CurrencyService ourInstance = new CurrencyService();

    public static CurrencyService getInstance(Context context) {
        mPrefs = MyPreferences.getInstance(context);
        return ourInstance;
    }

    private Double getDelta(Double latest, Double yester) {

        if (latest != null && yester != null)
            return ((latest - yester) * 100) / yester;
        else return null;
    }

    public interface ReturnBlock {
        void show(Double result, Double delta, Date date, Throwable t);
    }

    private Double getRateByType(CURRENCY_TYPE type, Currency currency) {
        Double latestCurrency = 0.0;
        if (currency != null) {
            switch (type) {
                case USD_RUB:
                    latestCurrency = currency.rates.rub;
                    break;
                case RUB_USD:
                    latestCurrency = 1 / currency.rates.rub;
                    break;
                case EUR_RUB:
                    latestCurrency = currency.rates.rub / currency.rates.eur;
                    break;
                case RUB_EUR:
                    latestCurrency = currency.rates.eur / currency.rates.rub;
                    break;
                case EUR_USD:
                    latestCurrency = 1 / currency.rates.eur;
                    break;
                case USD_EUR:
                    latestCurrency = currency.rates.eur;
                    break;

            }
            return latestCurrency;
        } else {
            return null;
        }
    }


    public void getRatesWithDelta(final CURRENCY_TYPE type, boolean withUpdate, final ReturnBlock block) {
        latestRates = mPrefs.readLatestRates();
        yesterRates = mPrefs.readYesterRates();

        ResponseBlock responseBlock = new ResponseBlock() {
            @Override
            public void blockResponse(Currency response, Throwable t, CURRENCY_DATE date) {

                try {
                    if (t != null)
                        block.show(null, null, null, t);
                    else
                        block.show(getRateByType(type, latestRates), getDelta(getRateByType(type, latestRates), getRateByType(type, yesterRates)), latestRates.updateTime, null);
                } catch (Exception e) {
                    block.show(null, null, null, t);
                }
            }
        };

        if ((latestRates == null && yesterRates == null) || withUpdate) //check availability
            update(CURRENCY_DATE.ALL, responseBlock);
        else if (yesterRates == null)
            update(CURRENCY_DATE.YESTERDAY, responseBlock);
        else if (latestRates == null)
            update(CURRENCY_DATE.LATEST, responseBlock);


        else if (latestRates.updateTime.before(DateSupport.getTimeHourAgo()) && yesterRates.updateTime.before(DateSupport.getTimeHourAgo())) //check the freshness
            update(CURRENCY_DATE.ALL, responseBlock);
        else if (latestRates.updateTime.before(DateSupport.getTimeHourAgo()))
            update(CURRENCY_DATE.LATEST, responseBlock);
        else if (yesterRates.updateTime.before(DateSupport.getYesterdayWithHourAgo()))
            update(CURRENCY_DATE.YESTERDAY, responseBlock);

        else
            responseBlock.blockResponse(latestRates, null, CURRENCY_DATE.LATEST);

    }


    private void sync(final ResponseBlock block) { //update both
        final boolean[] yesterDone = {false};
        final boolean[] latestDone = {false};
        final boolean[] isError = {false};


        ResponseBlock responseBlock = new ResponseBlock<Currency>() {
            @Override
            public void blockResponse(Currency response, Throwable t, CURRENCY_DATE date) {

                if (t != null || response == null) {
                    if (!isError[0]) {
                        isError[0] = true;
                        block.blockResponse(null, t, date);
                    }
                }

                if (date == CURRENCY_DATE.YESTERDAY)
                    yesterDone[0] = true;
                else if (date == CURRENCY_DATE.LATEST)
                    latestDone[0] = true;

                if (yesterDone[0] && latestDone[0] && !isError[0])
                    block.blockResponse(response, null, date);
//                    block.show(getRateByType(type,
//                                    CURRENCY_DATE.LATEST, mPrefs.readLatestRates()),
//                            getDelta(type),
//                            DateSupport.parseDate(mPrefs.readLatestRates().date),
//                            null);


            }
        };

//        ServerManager.getInstance().downloadCurrency(CURRENCY_DATE.YESTERDAY, responseBlock);
//        ServerManager.getInstance().downloadCurrency(CURRENCY_DATE.LATEST, responseBlock);

        update(CURRENCY_DATE.LATEST, responseBlock);
        update(CURRENCY_DATE.YESTERDAY, responseBlock);

    }


    private void update(CURRENCY_DATE date, final ResponseBlock responseBlock) { //апдейтит один в зависимости от даты
        if (date == CURRENCY_DATE.ALL) {
            sync(responseBlock);
        } else {
            downloadAndSave(date, responseBlock);
        }
    }

    private void downloadAndSave(CURRENCY_DATE date, final ResponseBlock responseBlock) {
        ServerManager.getInstance().downloadCurrency(date, new ResponseBlock<Currency>() {
            @Override
            public void blockResponse(Currency response, Throwable t, CURRENCY_DATE date) {
                if (response != null && !response.success) {
                    responseBlock.blockResponse(null, new Throwable("API ERROR"), null);
                    return;
                } else if (response == null || t != null) {
                    responseBlock.blockResponse(null, t, null);
                    return;
                }

                if (date == CURRENCY_DATE.YESTERDAY)
                    mPrefs.saveYesterRate(response);
                else if (date == CURRENCY_DATE.LATEST)
                    mPrefs.saveLatestRate(response);

                updateLocalRates();
                responseBlock.blockResponse(response, null, date);
            }
        });
    }

    private void updateLocalRates(){
        latestRates = mPrefs.readLatestRates();
        yesterRates = mPrefs.readYesterRates();
    }

}
