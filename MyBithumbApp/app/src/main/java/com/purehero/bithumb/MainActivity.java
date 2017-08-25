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

                tvPrice.setText( "Loading" );
                tvDate.setText( "Loading" );
                tvTransactionsCount.setText( "Loading" );
                tvDateValue.setText( "Loading" );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvPrice = ( TextView ) findViewById( R.id.txCurrentPrice );
        tvDate  = ( TextView ) findViewById( R.id.txCurrentDate );
        tvTransactionsCount = ( TextView ) findViewById( R.id.txTransactionsCount );
        tvDateValue = ( TextView ) findViewById( R.id.txDateValue );

        pollingHandler.postDelayed ( pollingHandlerRunnable, 10000 );
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

    Handler pollingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){   // Message id 가 0 이면

            }
        }
    };

    Runnable pollingHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            PollingTask pollingTask = new PollingTask();
            pollingTask.execute();
        }
    };

    class PollingTask extends AsyncTask {
        BithumbRecentTransactions recentTransactions = new BithumbRecentTransactions();

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected Object doInBackground(Object[] objects) {
            String strTransactions = getBithumbCurrencyInfo( CURRENCY.XRP );
            Log.d( "TEST", ">>>> " +  strTransactions );

            try {
                recentTransactions.reload( strTransactions );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return recentTransactions;
        }

        @Override
        protected void onPostExecute(Object o) {
            BithumbRecentTransactionsData data = recentTransactions.getLastData();
            long price = data.getPrice();

            tvPrice.setText(String.valueOf(price));
            tvDate.setText(data.getTransaction_date());
            tvTransactionsCount.setText( String.valueOf( recentTransactions.getTransactionCount()));
            tvDateValue.setText( String.valueOf( data.getTransaction_date_value()));

            pollingHandler.postDelayed(pollingHandlerRunnable, 10000);
            super.onPostExecute(o);
        }

        private String getBithumbCurrencyInfo( CURRENCY currency ) {
            return HttpRequest.get( "https://api.bithumb.com/public/recent_transactions/" + m_reqCurrency );
        }
    };

    class BithumbRecentTransactions {
        int status;                 // 결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
        List<BithumbRecentTransactionsData> datas = new ArrayList<BithumbRecentTransactionsData>();

        public void reload( String strJson ) throws JSONException {
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
