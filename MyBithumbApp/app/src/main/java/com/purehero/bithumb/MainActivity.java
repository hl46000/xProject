package com.purehero.bithumb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayAdapter sAdapter;
    TextView tvPrice = null;
    TextView tvDate = null;
    TextView tvTransactionsCount = null;
    TextView tvDateValue = null;

    String m_reqCurrency = "BTC";
    final int ONE_SECOND_POLLING_TIME = 1000;                           // ms
    final int TEN_SECOND_POLLING_TIME = 10 * ONE_SECOND_POLLING_TIME;   // ms
    final int ONE_MINUTE_POLLING_TIME =  6 * TEN_SECOND_POLLING_TIME;   // ms
    final int TEN_MINUTE_POLLING_TIME = 10 * ONE_MINUTE_POLLING_TIME;   // ms

    TenSecondDBTable oneSecondDB = null;
    TenSecondDBTable tenSecondDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.cmbCurrency);
        sAdapter = ArrayAdapter.createFromResource(this, R.array.CURRENCY, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter( sAdapter );
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_reqCurrency = ((String) sAdapter.getItem( position )).substring(0,3).trim();
                callTenSecondPollingFunction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvPrice = ( TextView ) findViewById( R.id.txCurrentPrice );
        tvDate  = ( TextView ) findViewById( R.id.txCurrentDate );
        tvTransactionsCount = ( TextView ) findViewById( R.id.txTransactionsCount );
        tvDateValue = ( TextView ) findViewById( R.id.txDateValue );

        oneSecondDB = new TenSecondDBTable( this, "one_second_data.db", 1 );
        tenSecondDB = new TenSecondDBTable( this, "ten_second_data.db", 1 );

        callOneSecondPollingFunction();
        callTenMinutePollingFunction();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private synchronized void callOneSecondPollingFunction() {
        oneSecondPollingHandler.postDelayed( tenSecondPollingHandlerRunnable, ONE_SECOND_POLLING_TIME );
    }
    private synchronized void callTenSecondPollingFunction() {
        tenSecondPollingHandler.postDelayed( tenSecondPollingHandlerRunnable, TEN_SECOND_POLLING_TIME );
    }

    private synchronized void callTenMinutePollingFunction() {
        tenMinutePollingHandler.postDelayed( tenMinutePollingHandlerRunnable, TEN_MINUTE_POLLING_TIME );
    }


    Handler oneSecondPollingHandler = new Handler();
    Handler tenSecondPollingHandler = new Handler();
    Handler tenMinutePollingHandler = new Handler();

    Runnable oneSecondPollingHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            new oneSecondPollingTask().execute();
        }
    };

    Runnable tenSecondPollingHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            new tenSecondPollingTask().execute();
        }
    };

    Runnable tenMinutePollingHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            new tenMinutePollingTask().execute();
        }
    };

    class oneSecondPollingTask extends AsyncTask {
        protected BithumbRecentTransactions recentTransactions = new BithumbRecentTransactions();

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d( G.LOG_TAG, "ONE SECOND - doInBackground " );

            String strTransactions = getBithumbCurrencyInfo( m_reqCurrency );
            Log.d( G.LOG_TAG, ">>>> " +  strTransactions );

            try {
                recentTransactions.reload( m_reqCurrency, strTransactions );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return recentTransactions;
        }

        @Override
        protected void onPostExecute(Object o) {
            BithumbRecentTransactionsData data = recentTransactions.getLastData();
            uiUpdate( recentTransactions, data );

            oneSecondDB.insertData( recentTransactions.getCurrency(), data.getTransaction_date_value(), data.getPrice());

            callOneSecondPollingFunction();
            super.onPostExecute(o);
        }

        private String getBithumbCurrencyInfo( String currency ) {
            return HttpRequest.get( "https://api.bithumb.com/public/recent_transactions/" + currency );
        }
    }

    class tenSecondPollingTask extends oneSecondPollingTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d( G.LOG_TAG, "TEN SECOND - doInBackground " );
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            /*BithumbRecentTransactionsData data = recentTransactions.getLastData();
            uiUpdate( recentTransactions, data );

            tenSecondDB.insertData( recentTransactions.getCurrency(), data.getTransaction_date_value(), data.getPrice());*/

            callTenSecondPollingFunction();
            super.onPostExecute(o);
        }
    };

    class oneMinutePollingTask extends oneSecondPollingTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d( G.LOG_TAG, "TEN SECOND - doInBackground " );
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            /*BithumbRecentTransactionsData data = recentTransactions.getLastData();
            uiUpdate( recentTransactions, data );

            tenSecondDB.insertData( recentTransactions.getCurrency(), data.getTransaction_date_value(), data.getPrice());*/

            callTenSecondPollingFunction();
            super.onPostExecute(o);
        }
    };

    class tenMinutePollingTask extends AsyncTask {
        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d( G.LOG_TAG, "TEN MINUTE - doInBackground " );
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            callTenMinutePollingFunction();
            super.onPostExecute(o);
        }
    };

    private void uiUpdate(BithumbRecentTransactions recentTransactions, BithumbRecentTransactionsData data) {
        long price = data.getPrice();

        tvPrice.setText(String.valueOf(price));
        tvDate.setText(data.getTransaction_date());
        tvTransactionsCount.setText( String.valueOf( recentTransactions.getTransactionCount()));
        tvDateValue.setText( String.valueOf( data.getTransaction_date_value()));
    }



    class BithumbRecentTransactions {
        String currency;

        int status;                 // 결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
        List<BithumbRecentTransactionsData> datas = new ArrayList<BithumbRecentTransactionsData>();

        public void reload( String _currency, String strJson ) throws JSONException {
            currency        = _currency;
            JSONObject json = new JSONObject( strJson );

            int status = json.getInt("status");
            if( status != 0 ) return;

            datas.clear();

            JSONArray dataArray = json.getJSONArray("data");
            for( int i = 0; i < dataArray.length(); i++ ) {
                datas.add( new BithumbRecentTransactionsData( dataArray.getJSONObject(i)));
            }
        }

        public BithumbRecentTransactionsData getLastData() {
            return datas.get(0);
        }

        public int getTransactionCount() {
            return datas.size();
        }
        public String getCurrency()      { return currency; }
    };

    class BithumbRecentTransactionsData {
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
    };

    final String CURRENCY_STRINGS[] = { "BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "ALL" };
    enum CURRENCY {
        BTC, ETH, DASH, LTC, ETC, XRP, BCH, ALL
    };


}
