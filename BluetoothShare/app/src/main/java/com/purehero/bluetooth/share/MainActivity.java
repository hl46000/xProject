package com.purehero.bluetooth.share;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.purehero.bluetooth.share.apps.ApkListFragment;
import com.purehero.bluetooth.share.contacts.ContactFragment;
import com.purehero.module.fragment.FragmentEx;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.refresh);
        fab.setOnClickListener( this );


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area);
            if( fragment instanceof FragmentEx ) {
                if( ((FragmentEx) fragment).onBackPressed()) {
                    return;
                }
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id ) {
            case R.id.action_settings:
                startActivity(new Intent( this, SettingActivity.class));
                return true;

            case R.id.action_bluetooth_admin :
                startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                return true;

            case R.id.action_bluetooth_identity :
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        int id = item.getItemId();
        switch( id ) {
            case R.id.nav_apps      : fragmentTransaction.replace(R.id.fragment_area, new ApkListFragment().setMainActivity(this)); break;
            case R.id.nav_contact   : fragmentTransaction.replace(R.id.fragment_area, new ContactFragment().setMainActivity( this )); break;
            case R.id.nav_files     : fragmentTransaction.replace(R.id.fragment_area, new FragmentText().setText("Files Fragments")); break;
            case R.id.nav_audios    : fragmentTransaction.replace(R.id.fragment_area, new FragmentText().setText("Audios Fragments")); break;
            case R.id.nav_documents : fragmentTransaction.replace(R.id.fragment_area, new FragmentText().setText("Documents Fragments")); break;
            case R.id.nav_photos    : fragmentTransaction.replace(R.id.fragment_area, new FragmentText().setText("Photos Fragments")); break;
            case R.id.nav_videos    : fragmentTransaction.replace(R.id.fragment_area, new FragmentText().setText("Videos Fragments")); break;
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if( requestCode == ACTIVITY_PERMISSION_REQUEST ) {
            checkPermission() ;
        }
    }

    final int ACTIVITY_PERMISSION_REQUEST = 1004;
    private int checkPermission() {
        String permissions[] = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        List<String> request_permissions = new ArrayList<String>();
        for( String permission : permissions ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }
        if( request_permissions.size() > 0 ) {
            ActivityCompat.requestPermissions(this, permissions, ACTIVITY_PERMISSION_REQUEST );

            return request_permissions.size();
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area);
        if( fragment instanceof View.OnClickListener ) {
            ((View.OnClickListener) fragment).onClick( view );
        }

        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
