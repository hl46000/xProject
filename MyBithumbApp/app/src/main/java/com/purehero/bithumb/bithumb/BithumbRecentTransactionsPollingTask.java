package com.purehero.bithumb.bithumb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.purehero.bithumb.G;
import com.purehero.bithumb.utils.HttpRequest;

import org.json.JSONException;

/**
 * Created by MY on 2017-08-26.
 */

public class BithumbRecentTransactionsPollingTask {
    final int RECENT_TRANSACTION_POLLING_TIME = 5 * 1000;   // 5sec

    private BithumbRecentTransactionsDbTable oneSecondDB = null;
    private BithumbRecentTransactions recentTransactions = null;
    private BithumbRecentTransactionsCallback callback = null;
    private String reqCurrency;
    private int reqPollingTime = RECENT_TRANSACTION_POLLING_TIME;

    public BithumbRecentTransactionsPollingTask(Context context ) {
        oneSecondDB         = new BithumbRecentTransactionsDbTable( context, "one_second_data.db", 1 );
        recentTransactions  = new BithumbRecentTransactions();
    }
    public void changeCurrency( String reqCurrency ) {
        this.reqCurrency = reqCurrency;
    }

    public void changePollingTime( int reqPollingTime ) {
        this.reqPollingTime = reqPollingTime;
    }

    public void excute( String reqCurrency, int pollingTime ) {
        changeCurrency( reqCurrency );
        changePollingTime( pollingTime );

        callOneSecondPollingFunction();
    }

    public void setOnRecentTransactionCallback( BithumbRecentTransactionsCallback callback ) {
        this.callback = callback;
    }

    class pollingTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d(G.LOG_TAG, "ONE SECOND - doInBackground ");

            String strTransactions = getBithumbCurrencyInfo(reqCurrency);
            Log.d(G.LOG_TAG, ">>>> " + strTransactions);

            try {
                recentTransactions.reload(reqCurrency, strTransactions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return recentTransactions;
        }

        @Override
        protected void onPostExecute(Object o) {
            if( callback != null ) {
                callback.callback( recentTransactions );
            }

            oneSecondDB.insertData(
                    recentTransactions.getCurrency(),
                    recentTransactions.getTransaction_date_value(),
                    recentTransactions.getPrice());

            callOneSecondPollingFunction();
            super.onPostExecute(o);
        }
    }

    private String getBithumbCurrencyInfo( String currency ) {
        return HttpRequest.get( "https://api.bithumb.com/public/recent_transactions/" + currency );
    }

    private synchronized void callOneSecondPollingFunction() {
        oneSecondPollingHandler.postDelayed( oneSecondPollingHandlerRunnable, reqPollingTime );
    }

    Handler oneSecondPollingHandler = new Handler();
    Runnable oneSecondPollingHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            new pollingTask().execute();
        }
    };
}
