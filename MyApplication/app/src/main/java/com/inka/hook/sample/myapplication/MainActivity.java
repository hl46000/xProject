package com.inka.hook.sample.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.purehero.module.appcompattabactivity.AppCompatTabActivity;
import com.purehero.module.appcompattabactivity.AppCompatTabViewPagerAdapter;
import com.purehero.module.appcompattabactivity.FragmentText;
import com.purehero.module.common.CheckPermissionListener;
import com.purehero.module.common.OnBackPressedListener;
import com.purehero.module.filelistfragment.FileListFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatTabActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initTabModule();

        checkPermission();
    }

    @Override
    public void addTabItem(AppCompatTabViewPagerAdapter pagerAdapter) {
        pagerAdapter.addItem( new FileListFragment().setMainActivity(this), "Tab1" );
        pagerAdapter.addItem( new FragmentText(), "Tab2" );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            int cnt = getFragmentManager().getBackStackEntryCount();
            for( int i = 0; i < cnt; i++ ) {
                Fragment  fragment = (Fragment) getFragmentManager().getBackStackEntryAt(i);
                if( fragment instanceof OnBackPressedListener ) {
                    OnBackPressedListener listener = ( OnBackPressedListener ) fragment;
                    if( listener.onBackPressed()) {
                        return;
                    }
                }
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123 :
                boolean recheckPermission = false;
                if ( grantResults.length > 0 ) {
                    for( int result : grantResults ) {
                        if( result == PackageManager.PERMISSION_GRANTED ) {
                            recheckPermission = true;
                            break;
                        }
                    }
                }
                if( recheckPermission ) {
                    checkPermission();
                }

                break;
        }
    }

    private void checkPermission() {
        runOnUiThread( new Runnable(){
            @Override
            public void run() {
                Set<String> permissions = new HashSet<String>();            // 필요한 퍼미션들

                int cnt = getFragmentManager().getBackStackEntryCount();    // Fragment 마다 필요한 퍼미션 정보를 수집한다.
                for( int i = 0; i < cnt; i++ ) {
                    Fragment  fragment = (Fragment) getFragmentManager().getBackStackEntryAt(i);
                    if( fragment instanceof CheckPermissionListener) {
                        CheckPermissionListener listener = ( CheckPermissionListener ) fragment;
                        permissions.addAll( listener.requestPermissionList() );
                    }
                }

                Set<String> request_permissions = new HashSet<String>();        // 사용자 승인이 필요한 퍼미션들
                for( String permission : permissions ) {
                    if (ContextCompat.checkSelfPermission( MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED ) {
                        request_permissions.add( permission );
                    }
                }

                if( request_permissions.size() > 0 ) {
                    String permissionsList [] = new String[request_permissions.size()];
                    request_permissions.toArray(permissionsList);
                    ActivityCompat.requestPermissions( MainActivity.this, permissionsList, 123 );
                }
            }
        });
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
}
