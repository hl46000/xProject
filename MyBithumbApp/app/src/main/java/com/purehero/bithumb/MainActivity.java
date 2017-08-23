package com.purehero.bithumb;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.purehero.bithumb.MainActivity.CURRENCY.XRP;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
                new Thread( myTestRunnable ).start();
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

    private String getBithumbCurrencyInfo( CURRENCY currency ) {
        InputStream in = null;
        ByteArrayOutputStream baos = null;

        try {
            URL url = new URL("https://api.bithumb.com/public/ticker/" + CURRENCY_STRINGS[currency.ordinal()]);
            URLConnection urlConnection = url.openConnection();
            in = urlConnection.getInputStream();

            baos = new ByteArrayOutputStream();
            copyInputStreamToOutputStream(in, baos);

            byte byteArray [] = baos.toByteArray();
            return new String( byteArray, 0, byteArray.length );

        } catch( Exception e ) {
            e.printStackTrace();

        } finally {
            if( in != null ) {
                try { in.close(); } catch ( IOException e ){}
                in = null;
            }

            if( baos != null ) {
                try { baos.close(); } catch ( IOException e ){}
                baos = null;
            }
        }
        return null;
    }

    Runnable myTestRunnable = new Runnable() {

        @Override
        public void run() {
            String strCurrencyInfo = getBithumbCurrencyInfo( CURRENCY.ALL );
            Log.d( "TEST", ">>>> " +  strCurrencyInfo );

            try {
                JSONObject json = new JSONObject( strCurrencyInfo );

                // {"status":"0000","data":{
                // "BTC":{"opening_price":"4625000","closing_price":"4418000","min_price":"4090000","max_price":"4655000","average_price":"4435401.5138","units_traded":"32014.21812327","volume_1day":"32014.21812327","volume_7day":"228820.59661624","buy_price":"4418000","sell_price":"4421000"},
                // "ETH":{"opening_price":"378650","closing_price":"347600","min_price":"335400","max_price":"391050","average_price":"360392.5480","units_traded":"907338.51381387955174996","volume_1day":"907338.51381387955174996","volume_7day":"4507815.666466573214152560","buy_price":"347550","sell_price":"347600"},
                // "DASH":{"opening_price":"318900","closing_price":"339950","min_price":"291100","max_price":"357750","average_price":"321904.7360","units_traded":"95628.30217223","volume_1day":"95628.30217223","volume_7day":"983222.192139220000000000","buy_price":"337950","sell_price":"338000"},
                // "LTC":{"opening_price":"53990","closing_price":"52600","min_price":"50000","max_price":"54950","average_price":"52671.3116","units_traded":"541909.37921105","volume_1day":"541909.37921105","volume_7day":"4361818.296157530000000000","buy_price":"52590","sell_price":"52600"},
                // "ETC":{"opening_price":"16195","closing_price":"15840","min_price":"15500","max_price":"17990","average_price":"16295.1651","units_traded":"2329113.285344800573358","volume_1day":"2329113.285344800573358","volume_7day":"6748089.378433459754746000","buy_price":"15810","sell_price":"15840"},
                // "XRP":{"opening_price":"181","closing_price":"218","min_price":"179","max_price":"240","average_price":"211.9860","units_traded":"1917261100.591941","volume_1day":"1917261100.591941","volume_7day":"3065065511.020950000000000000","buy_price":"218","sell_price":"219"},
                // "BCH":{"opening_price":"714100","closing_price":"767800","min_price":"633000","max_price":"861000","average_price":"747488.4905","units_traded":"828259.49266315","volume_1day":"828259.49266315","volume_7day":"5980375.163381360000000000","buy_price":"767700","sell_price":"767800"},
                // "date":"1503412052017"}}
                int status = json.getInt("status");
                if( status != 0 ) return;

                JSONObject data = json.getJSONObject("data");
                CurrencyData currencyData = new CurrencyData( data, CURRENCY.XRP );
                Log.d( "TEST", currencyData.toDisplayString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    class CurrencyData {
        CURRENCY currency;
        long opening_price;
        long closing_price;
        double average_price;
        long min_price;
        long max_price;
        long date;

        public CurrencyData( JSONObject currencyJsonData, CURRENCY _currency ) throws JSONException {
            currency = _currency;
            JSONObject currency_data = currencyJsonData.getJSONObject( CURRENCY_STRINGS[currency.ordinal()] );

            opening_price   = currency_data.getLong( "opening_price" );
            closing_price   = currency_data.getLong( "closing_price" );
            min_price       = currency_data.getLong( "min_price" );
            max_price       = currency_data.getLong( "max_price" );
            average_price   = currency_data.getDouble( "average_price" );

            date            = currencyJsonData.getLong( "date" );
        }

        public String toDisplayString() {
            String ret = CURRENCY_STRINGS[currency.ordinal() ];
            ret += "[";

            ret += String.format( "opening_price:%d, ", opening_price );
            ret += String.format( "closing_price:%d, ", closing_price );
            ret += String.format( "min_price:%d, ", min_price );
            ret += String.format( "max_price:%d, ", max_price );
            ret += String.format( "average_price:%f,", average_price );

            ret += String.format( "date:%s", DateUtil.getModifiedDate( Locale.KOREA, date ));

            ret += "]";
            return ret;
        }

    };

    final String CURRENCY_STRINGS[] = { "BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "ALL" };
    enum CURRENCY {
        BTC, ETH, DASH, LTC, ETC, XRP, BCH, ALL
    };

    private void copyInputStreamToOutputStream(InputStream in, String msgTag ) throws IOException {
        InputStreamReader isr = new InputStreamReader( in );
        BufferedReader br = new BufferedReader( isr );

        String line;
        while(( line = br.readLine()) != null ) {
            Log.d( msgTag, line );
        }
    }

    private void copyInputStreamToOutputStream(InputStream in, OutputStream os ) throws IOException {
        byte buffer[] = new byte[ 10240 ];

        int nBytes = 0;
        while(( nBytes = in.read( buffer, 0, 10240 )) > 0 ) {
            os.write( buffer, 0, nBytes );
        }
    }
}
