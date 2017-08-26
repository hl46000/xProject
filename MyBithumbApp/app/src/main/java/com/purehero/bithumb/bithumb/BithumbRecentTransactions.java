package com.purehero.bithumb.bithumb;

import com.purehero.bithumb.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017-08-26.
 */

public class BithumbRecentTransactions {
    String currency;

    int status;                 // 결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)

    long price                  = 0;    // 1Currency 거래 금액
    String transaction_date     = "";   //	거래 채결 시간( 2015-04-17 11:36:13 )
    long transaction_date_value = 0;    //	거래 채결 시간( 2015-04-17 11:36:13 )

    //List<BithumbRecentTransactionsData> datas = new ArrayList<BithumbRecentTransactionsData>();

    public void reload( String _currency, String strJson ) throws JSONException {
        currency        = _currency;
        JSONObject json = new JSONObject( strJson );

        int status = json.getInt("status");
        if( status != 0 ) return;

        //datas.clear();
        price = 0;

        JSONArray dataArray = json.getJSONArray("data");
        for( int i = 0; i < dataArray.length(); i++ ) {
            JSONObject jsonData = dataArray.getJSONObject(i);
            BithumbRecentTransactionsData data = new BithumbRecentTransactionsData( jsonData );

            price                   += data.getPrice();
            transaction_date        = data.getTransaction_date();
            transaction_date_value  = data.getTransaction_date_value();

            //datas.add( data );
        }

        price /= dataArray.length();
    }

    //public int getTransactionCount() { return datas.size(); }
    public String getCurrency()      { return currency; }
    public long getPrice() { return price; }
    public String getTransaction_date() { return transaction_date; }
    public long getTransaction_date_value() { return transaction_date_value; }
}
