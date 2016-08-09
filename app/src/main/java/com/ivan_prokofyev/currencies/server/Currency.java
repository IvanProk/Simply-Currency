package com.ivan_prokofyev.currencies.server;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Prokofyev Ivan on 06.07.2016.
 */
public class Currency implements Serializable {


    public Date updateTime;

    @SerializedName("success")
    public boolean success;

    @SerializedName("source")
    public String source;

    @SerializedName("date")
    public String date;

    @SerializedName("quotes")
    public Rates rates;

    public class Rates implements Serializable {

        @SerializedName("USDRUB")
        public Double rub;

        @SerializedName("USDEUR")
        public Double eur;

    }

}
