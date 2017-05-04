package com.purehero.bluetooth.share;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.view.MenuItem;
import android.view.View;

import com.purehero.bluetooth.share.apps.ApkListFragment;
import com.purehero.bluetooth.share.contacts.ContactFragment;
import com.purehero.bluetooth.share.files.FileListFragment;
import com.purehero.module.fragment.FragmentEx;

import java.io.File;
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

        onHomeFragment();
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
            if(!( fragment instanceof HomeFragment )) {
                onHomeFragment();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area);
        if( fragment.onOptionsItemSelected( item )) {
            return true;
        }

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

    private void onHomeFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        HomeFragment fragment = new HomeFragment().setMainActivity(this);
        fragmentTransaction.replace(R.id.fragment_area, fragment );
        fragmentTransaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        replaceFragmentById(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragmentById( int id ) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        switch( id ) {
            case R.id.btnApps       :
            case R.id.nav_apps      : fragmentTransaction.replace(R.id.fragment_area, new ApkListFragment().setMainActivity(this)); break;
            case R.id.btnContacts   :
            case R.id.nav_contact   : fragmentTransaction.replace(R.id.fragment_area, new ContactFragment().setMainActivity( this )); break;
            case R.id.btnMyFiles    :
            case R.id.nav_files     :
                FileListFragment myFiles = new FileListFragment().setMainActivity(this);
                myFiles.setRootFolder( new File( "/"));
                fragmentTransaction.replace(R.id.fragment_area, myFiles ); break;

            case R.id.btnAudios     :
            case R.id.nav_audios    :
                FileListFragment myAudios = new FileListFragment().setMainActivity(this);
                myAudios.setRootFolder( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MUSIC ) );
                fragmentTransaction.replace(R.id.fragment_area, myAudios ); break;

            case R.id.btnDocuments  :
            case R.id.nav_documents :
                FileListFragment myDocuments = new FileListFragment().setMainActivity(this);
                myDocuments.setRootFolder( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS ) );
                fragmentTransaction.replace(R.id.fragment_area, myDocuments ); break;

            case R.id.btnPhotos     :
            case R.id.nav_photos    :
                FileListFragment myPhotos = new FileListFragment().setMainActivity(this);
                myPhotos.setRootFolder( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ) );
                fragmentTransaction.replace(R.id.fragment_area, myPhotos ); break;

            case R.id.btnVideos     :
            case R.id.nav_videos    :
                FileListFragment myVideos = new FileListFragment().setMainActivity(this);
                myVideos.setRootFolder( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES ) );
                fragmentTransaction.replace(R.id.fragment_area, myVideos ); break;
        }

        fragmentTransaction.commit();
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
