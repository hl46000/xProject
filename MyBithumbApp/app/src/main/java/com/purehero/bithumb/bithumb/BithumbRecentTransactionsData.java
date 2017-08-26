package com.purehero.bithumb.bithumb;

import com.purehero.bithumb.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MY on 2017-08-26.
 */

public class BithumbRecentTransactionsData {
    String transaction_date;        //	거래 채결 시간( 2015-04-17 11:36:13 )
    long transaction_date_value;    //	거래 채결 시간( 2015-04-17 11:36:13 )
    String type;                // 판/구매 (ask, bid)
    double units_traded;        // 거래 Currency 수량
    long price;                  // 1Currency 거래 금액
    long total;                  // 총 거래금액

    public BithumbRecentTransactionsData( JSONObject jsonObject ) throws JSONException {
        transaction_date    = jsonObject.getString("transaction_date");
        type                = jsonObject.getString("type");
        units_traded        = jsonObject.getDouble("units_traded");
        price               = jsonObject.getLong("price");
        total               = jsonObject.getLong("total");

        transaction_date_value = DateUtil.ConvertToDate( transaction_date, "yyyy-MM-dd hh:mm:ss" ).getTime();
    }

    public long getPrice() { return price; }
    public String getTransaction_date() { return transaction_date; }
    public long getTransaction_date_value() { return transaction_date_value; }
}
