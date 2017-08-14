package com.purehero.bluetooth.share;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.Toast;

import com.purehero.bluetooth.share.apps.ApkListFragment;
import com.purehero.bluetooth.share.categorys.AudioListAdapter;
import com.purehero.bluetooth.share.categorys.MyFileListAdapter;
import com.purehero.bluetooth.share.categorys.VideoListAdapter;
import com.purehero.bluetooth.share.contacts.ContactFragment;
import com.purehero.bluetooth.share.files.FileListFragment;
import com.purehero.bluetooth.share.categorys.ImageListAdapter;
import com.purehero.module.fragment.FragmentEx;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FloatingActionButton fab = null;
    ActionBarDrawerToggle mDrawerToggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;

    private StartAppAd startAppAd = null;

    private static BluetoothAdapter btAdapter 	= null;
    public static BluetoothAdapter getBtAdapter() { return btAdapter; }

    public static final int REQUEST_ENABLE_BT 				= 1023;

    private static MainActivity instance = null;
    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "206482942", true);
        StartAppAd.disableSplash();

        startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {

            }
        });

        instance = this;

        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if( btAdapter != null ) {
            if (!btAdapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText( this, R.string.bluetooth_not_enable, Toast.LENGTH_LONG ).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        fab = (FloatingActionButton) findViewById(R.id.refresh);
        fab.setOnClickListener( this );


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkPermission();

        onHomeFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area);
        fragment.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
                private long backPressedTime = 0;

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

            if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
                //StartAppAd.onBackPressed(this);
                super.onBackPressed();

            } else {
                backPressedTime = System.currentTimeMillis();
                Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_area);
        if( fragment.onOptionsItemSelected( item )) {
            return true;
        }

        /*
        int id = item.getItemId();
        switch (id ) {
            case R.id.action_settings:
                openSettingPage();
                return true;

            case R.id.action_bluetooth_admin :
                openBluetoothAdminPage();
                return true;

            case R.id.action_bluetooth_identity :
                return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    public void openSettingPage() {
        startActivity(new Intent( this, SettingActivity.class));
    }

    public void openBluetoothAdminPage() {
        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    private void onHomeFragment() {
        try {
            replaceFragmentById( HOME_FRAGMENT );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {
            replaceFragmentById(item.getItemId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private HomeFragment    homeFragment        = null;
    private ApkListFragment apkListFragment     = null;
    private ContactFragment contactListFragment = null;
    private BaseListFragment audioListFragment  = null;
    private BaseListFragment photoListFragment  = null;
    private BaseListFragment videoListFragment  = null;

    private final int HOME_FRAGMENT             = 10000;
    public void replaceFragmentById( int id ) throws Exception {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        switch( id ) {
            case R.id.nav_show_ads :
                startAppAd.showAd();
                startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO);
                //StartAppAd.showAd(this);
                break;

            case HOME_FRAGMENT      :
                fab.setVisibility( View.INVISIBLE );
                if( homeFragment == null ) {
                    homeFragment = new HomeFragment();
                }
                fragmentTransaction.replace(R.id.fragment_area, homeFragment );
                break;

            case R.id.btnApps       :
            case R.id.nav_apps      :
                fab.setVisibility( View.VISIBLE );
                if( apkListFragment == null ) {
                    apkListFragment = new ApkListFragment();
                }
                fragmentTransaction.replace(R.id.fragment_area, apkListFragment );
                break;

            case R.id.btnContacts   :
            case R.id.nav_contact   :
                fab.setVisibility( View.VISIBLE );
                if( contactListFragment == null ) {
                    contactListFragment = new ContactFragment();
                }
                fragmentTransaction.replace(R.id.fragment_area, contactListFragment );
                break;

            case R.id.btnMyFiles    :
            case R.id.nav_files     :
                fab.setVisibility( View.VISIBLE );
                BaseListFragment myFiles = new BaseListFragment().setBaseListAdapter(new MyFileListAdapter(this)).setMainActivity(this, R.string.my_files);
                fragmentTransaction.replace(R.id.fragment_area, myFiles ); break;

            case R.id.btnAudios     :
            case R.id.nav_audios    :
                fab.setVisibility( View.VISIBLE );
                if( audioListFragment == null ) {
                    audioListFragment = new BaseListFragment().setBaseListAdapter(new AudioListAdapter(this)).setMainActivity(this, R.string.audios );
                }
                fragmentTransaction.replace(R.id.fragment_area, audioListFragment );
                break;

            case R.id.btnDocuments  :
            case R.id.nav_documents :
                FileListFragment myDocuments = new FileListFragment().setMainActivity(this);
                myDocuments.setRootFolder( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS ) );
                fragmentTransaction.replace(R.id.fragment_area, myDocuments ); break;

            case R.id.btnPhotos     :
            case R.id.nav_photos    :
                fab.setVisibility( View.VISIBLE );
                if( photoListFragment == null ) {
                    photoListFragment = new BaseListFragment().setBaseListAdapter(new ImageListAdapter(this)).setMainActivity(this, R.string.photos);
                }
                fragmentTransaction.replace(R.id.fragment_area, photoListFragment );
                break;

            case R.id.btnVideos     :
            case R.id.nav_videos    :
                fab.setVisibility( View.VISIBLE );
                if( videoListFragment == null ) {
                    videoListFragment = new BaseListFragment().setBaseListAdapter(new VideoListAdapter(this)).setMainActivity(this, R.string.videos);
                }
                fragmentTransaction.replace(R.id.fragment_area, videoListFragment );
                break;
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

    /**
     * To be semantically or contextually correct, maybe change the name
     * and signature of this function to something like:
     *
     * private void showBackButton(boolean show)
     * Just a suggestion.
     */
    public void showActionBarBackButton(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if(enable) {
            // Remove hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if(!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }
}
