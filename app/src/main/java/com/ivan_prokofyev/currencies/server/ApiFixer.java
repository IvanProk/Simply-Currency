package com.ivan_prokofyev.currencies.server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiraha on 06.07.2016.
 */
public interface ApiFixer {

//
//    @GET("/historical?{API_KEY}")
//    Call<Currency> getRates(@Path("key") String API_KEY);

    @GET("/api/historical")
    Call<Currency> getYesterRates(@Query("access_key") String API_KEY, @Query("date") String date, @Query("currencies") String currencies, @Query("format") int format);
}
