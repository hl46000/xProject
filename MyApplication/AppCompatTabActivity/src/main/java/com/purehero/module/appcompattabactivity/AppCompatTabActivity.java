package com.purehero.module.appcompattabactivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.purehero.module.common.CheckPermissionListener;

import java.util.HashSet;
import java.util.Set;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by purehero on 2017-03-22.
 */

abstract public class AppCompatTabActivity extends AppCompatActivity implements MaterialTabListener {
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private AppCompatTabViewPagerAdapter pagerAdapter;

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission();
    }

    public void initTabModule() {
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        pagerAdapter = new AppCompatTabViewPagerAdapter(getSupportFragmentManager(), tabHost );
        addTabItem( pagerAdapter );

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
    }

    abstract public void addTabItem(AppCompatTabViewPagerAdapter pagerAdapter);

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        AppCompatTabFragment fragment = (AppCompatTabFragment) pagerAdapter.getItem( pager.getCurrentItem());
        if( !fragment.onBackPressed()) {
            if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();

            } else {
                backPressedTime = System.currentTimeMillis();
                Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
            }
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

    protected void checkPermission() {
        Log.d( "MyLOG", "checkPermission()" );
        Set<String> permissions = new HashSet<String>();            // 필요한 퍼미션들

        int cnt = pagerAdapter.getCount();    // Fragment 마다 필요한 퍼미션 정보를 수집한다.
        for( int i = 0; i < cnt; i++ ) {
            Fragment fragment = (Fragment) pagerAdapter.getItem(i);
            if( fragment instanceof CheckPermissionListener) {
                CheckPermissionListener listener = (CheckPermissionListener) fragment;
                permissions.addAll( listener.requestPermissionList() );
            }
        }

        Set<String> request_permissions = new HashSet<String>();        // 사용자 승인이 필요한 퍼미션들
        for( String permission : permissions ) {
            Log.d( "MyLOG", String.format( "Permission : %s", permission ) );

            if (ContextCompat.checkSelfPermission( AppCompatTabActivity.this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }

        if( request_permissions.size() > 0 ) {
            String permissionsList [] = new String[request_permissions.size()];
            request_permissions.toArray(permissionsList);
            ActivityCompat.requestPermissions( AppCompatTabActivity.this, permissionsList, 123 );
        }
    }
}
