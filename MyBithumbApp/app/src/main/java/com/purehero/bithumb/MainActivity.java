package com.purehero.bithumb;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.purehero.bithumb.bithumb.BithumbRecentTransactions;
import com.purehero.bithumb.bithumb.BithumbRecentTransactionsCallback;
import com.purehero.bithumb.bithumb.BithumbRecentTransactionsPollingTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvPrice = null;
    TextView tvDate = null;
    TextView tvTransactionsCount = null;
    TextView tvDateValue = null;

    String m_reqCurrency = "BTC";
    int m_reqPollingTime = 1000;        // 1sec ( 1000ms )
    BithumbRecentTransactionsPollingTask recentTransactionPollingTask = null;

    ArrayAdapter currencyAdapter;
    ArrayAdapter pollingTimeAdapter;

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

        init_currency_adapter();
        init_polling_time_adapter();
        
        tvPrice = ( TextView ) findViewById( R.id.txCurrentPrice );
        tvDate  = ( TextView ) findViewById( R.id.txCurrentDate );
        tvTransactionsCount = ( TextView ) findViewById( R.id.txTransactionsCount );
        tvDateValue = ( TextView ) findViewById( R.id.txDateValue );

        recentTransactionPollingTask = new BithumbRecentTransactionsPollingTask( this );
        recentTransactionPollingTask.setOnRecentTransactionCallback( recentTransactionsCallback );
        recentTransactionPollingTask.excute( m_reqCurrency, m_reqPollingTime );
    }

    private void init_polling_time_adapter() {
        Spinner spinner = (Spinner) findViewById(R.id.cmbPollingTime);
        pollingTimeAdapter = ArrayAdapter.createFromResource(this, R.array.POLLING_TIME, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(pollingTimeAdapter);
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_reqPollingTime = 1000 * Integer.valueOf(((String) pollingTimeAdapter.getItem( position )).split(" ")[0].trim());
                recentTransactionPollingTask.changePollingTime( m_reqPollingTime );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init_currency_adapter() {
        Spinner spinner = (Spinner) findViewById(R.id.cmbCurrency);
        currencyAdapter = ArrayAdapter.createFromResource(this, R.array.CURRENCY, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(currencyAdapter);
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_reqCurrency = ((String) currencyAdapter.getItem( position )).split(" ")[0].trim();
                recentTransactionPollingTask.changeCurrency( m_reqCurrency );
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

    BithumbRecentTransactionsCallback recentTransactionsCallback = new BithumbRecentTransactionsCallback() {
        @Override
        public void callback( final BithumbRecentTransactions recentTransactions) {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    uiUpdate( recentTransactions );
                }
            });
        }
    };

    private void uiUpdate(BithumbRecentTransactions recentTransactions ) {
        long price = recentTransactions.getPrice();

        tvPrice.setText(String.valueOf(price));
        tvDate.setText(recentTransactions.getTransaction_date());
        tvDateValue.setText( String.valueOf( recentTransactions.getTransaction_date_value()));
    }
}
