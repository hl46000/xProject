package com.purehero.module.tabhost;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.purehero.module.common.R;
import com.purehero.module.fragment.CheckPermissionListener;
import com.purehero.module.fragment.FragmentEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by purehero on 2017-03-27.
 */

    public abstract class TabAppCompatActivity extends AppCompatActivity {
        private TabLayout tabHost;
        private ViewPager pager;
        private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
    }

    @Override
        public void setContentView(@LayoutRes int layoutResID) {
            super.setContentView(layoutResID);
            initTabView();
        }

        @Override
        public void setContentView(View view) {
            super.setContentView(view);
            initTabView();
        }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initTabView();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
        initTabView();
    }

    private void initTabView() {
        tabHost = (TabLayout) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabHost, pager );
        addTabItem( pagerAdapter );

        pager.setAdapter(pagerAdapter);
    }

    protected abstract void addTabItem(ViewPagerAdapter pagerAdapter);

    public int getTabCount() {
        return pagerAdapter.getCount();
    }
    public void addTab(FragmentEx fragment, String title ) {
        pagerAdapter.addItem( fragment, title );
        pagerAdapter.notifyDataSetChanged();
    }

    public void removeTab( int tabIndex ) {
        pagerAdapter.removeItem( tabIndex );
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123 :
                boolean recheckPermission = false;
                for( int i = 0; i < grantResults.length; i++ ) {

                }
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

    List<String> request_permissions = new ArrayList<String>();
    private int checkPermission() {
        request_permissions.clear();

        for( int i = 0; i < pagerAdapter.getCount(); i++ ) {
            Object fragment = pagerAdapter.getItem(i);
            if( fragment instanceof CheckPermissionListener ) {
                CheckPermissionListener reqPermission = ( CheckPermissionListener ) fragment;
                List<String> reqPermissions = reqPermission.requestPermissionList();
                for( String permission : reqPermissions ) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                        request_permissions.add( permission );
                    }
                }
            }
        }

        if( request_permissions.size() > 0 ) {
            String permissionsList [] = new String[request_permissions.size()];
            request_permissions.toArray(permissionsList);
            ActivityCompat.requestPermissions(this, permissionsList, 123 );
        }

        return request_permissions.size();
    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        FragmentEx fragment = ( FragmentEx ) pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment.onBackPressed()) {
            return;
        }

        if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();

        } else {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FragmentEx fragment = ( FragmentEx ) pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment.onCreateOptionsMenu( menu )) {
            return true;
        }
        return false;
    }

    //onPrepareOptionsMenu

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FragmentEx fragment = ( FragmentEx ) pagerAdapter.getItem( pager.getCurrentItem());
        fragment.onPrepareOptionsMenu( menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentEx fragment = ( FragmentEx ) pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment.onOptionsItemSelected( item )) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
