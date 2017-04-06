package com.purehero.apk.extractor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
import com.purehero.common.FragmentEx;
import com.purehero.common.FragmentText;
import com.purehero.common.ViewPagerAdapter;
import com.purehero.file.browser.FileListData;
import com.purehero.file.browser.FileListFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MaterialTabListener {

    //private AdView bannerAdView = null;
    //private InterstitialAd interstitialAd	= null;	// 전면 광고
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/

        //
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabHost );
        pagerAdapter.addItem( new ApkListFragment().setMainActivity(this), R.string.application );

        String state= Environment.getExternalStorageState(); //외부저장소(SDcard)의 상태 얻어오기
        if( state.equals(Environment.MEDIA_MOUNTED)){ // SDcard 의 상태가 쓰기 가능한 상태로 마운트되었는지 확인
            File externalStorageFolder = Environment.getExternalStorageDirectory();
            pagerAdapter.addItem( new FileListFragment().setRootFolder(externalStorageFolder).setMainActivity(this), R.string.sdcard_name );
        }

        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
        // insert all tabs from pagerAdapter data
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(pagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        /*
        bannerAdView = (AdView) findViewById(R.id.adView);
        if( bannerAdView != null ) {
            bannerAdView.setVisibility( View.GONE );

            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAdView.setAdListener( new AdListener(){
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    //if( !G.debuggable ) // 릴리즈 빌드이거나 테스트 할때만 아래를 활성화 시킨다.
                    {
                        bannerAdView.setVisibility(View.VISIBLE);
                    }
                }});

            bannerAdView.loadAd(adRequest);
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        //RegisterSdCardUpdateReceiver();
        //showFullAd();
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

                for( int i = 0; i < pagerAdapter.getCount(); i++ ) {
                    FragmentEx fragment = (FragmentEx) pagerAdapter.getItem( i );
                    if( fragment instanceof FileListFragment ) {
                        FileListFragment f = ( FileListFragment ) fragment;
                        f.reloadListView();
                    }
                }
                break;
        }
    }

    private void checkPermission() {
        String permissions[] = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        List<String> request_permissions = new ArrayList<String>();
        for( String permission : permissions ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }

        if( request_permissions.size() > 0 ) {
            String permissionsList [] = new String[request_permissions.size()];
            request_permissions.toArray(permissionsList);
            ActivityCompat.requestPermissions(this, permissionsList, 123 );
        }
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
            FragmentEx fragment = (FragmentEx) pagerAdapter.getItem( pager.getCurrentItem());
            if( !fragment.onBackPressed()) {
                if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();

                } else {
                    backPressedTime = System.currentTimeMillis();
                    Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
                }
            }
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
            startActivity( new Intent( this, SettingsActivity.class ));
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

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {}

    @Override
    public void onTabUnselected(MaterialTab tab) {
        //showFullAd();
    }

    /*
    public void showFullAd() {
        if( interstitialAd != null ) {
            if( interstitialAd.isLoaded()) {
                if( System.currentTimeMillis() % 10 == 2 )  // 1/16 확률로 전체 광고 표시
                {
                    interstitialAd.show();
                }
            }
            return;
        }

        interstitialAd = new InterstitialAd( this );
        interstitialAd.setAdUnitId( getResources().getString(R.string.fullscreen_ad_unit_id) );
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener( new AdListener(){

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                AdRequest adRequest = new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequest);
            }
        });
    }
    */

    ///////////////////////////////////////////////
    public void changeFileListModeToolbar() {
    }

    public void clearSelectedItems(boolean b) {
    }

    public void setSelectToolbarSelectedCount(int selectedCount) {
    }

    public int getOpCode() {
        return R.string.copying;
    }

    public List<FileListData> getSelectedItems() {
        return null;
    }

    public void collectSelectedItems() {
    }

    public void setOpCode(int action_move) {
    }

    public void changeFileSelectModeToolbar() {
    }
}
