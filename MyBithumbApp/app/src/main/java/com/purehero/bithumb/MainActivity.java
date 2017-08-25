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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

    ArrayAdapter sAdapter;

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

        Spinner spinner = (Spinner) findViewById(R.id.cmbCurrency);
        sAdapter = ArrayAdapter.createFromResource(this, R.array.CURRENCY, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter( sAdapter );;
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText( MainActivity.this, (String) sAdapter.getItem( position ), Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        return HttpRequest.get( "https://api.bithumb.com/public/recent_transactions/" + CURRENCY_STRINGS[currency.ordinal()] );
    }

    Runnable myTestRunnable = new Runnable() {

        @Override
        public void run() {
            String strCurrencyInfo = getBithumbCurrencyInfo( CURRENCY.ALL );
            Log.d( "TEST", ">>>> " +  strCurrencyInfo );

            try {
                JSONObject json = new JSONObject( strCurrencyInfo );

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


}
