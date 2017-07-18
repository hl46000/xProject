package com.purehero.module.tabhost;

import android.Manifest;
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
import android.util.Log;
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
    protected TabLayout tabHost;
    protected ViewPager pager;
    protected ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d( "MyLOG", "TabAppCompatActivity::onCreate");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d( "MyLOG", "TabAppCompatActivity::onPostCreate");
    }

    @Override
    protected void onPostResume() {
        Log.d( "MyLOG", "TabAppCompatActivity::onPostResume");
        super.onPostResume();
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
        Log.d( "MyLOG", "TabAppCompatActivity::initTabView");

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


    protected int checkPermission() {
        List<String> request_permissions = new ArrayList<String>();

        for( int i = 0; i < pagerAdapter.getCount(); i++ ) {
            Object fragment = pagerAdapter.getItem(i);
            if( fragment instanceof CheckPermissionListener ) {
                CheckPermissionListener reqPermission = ( CheckPermissionListener ) fragment;
                List<String> reqPermissions = reqPermission.requestPermissionList();
                if( reqPermissions != null ) {
                    for( String permission : reqPermissions ) {
                        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                            request_permissions.add( permission );
                        }
                    }
                }
            }
        }

        Log.d( "MyLOG", "" + request_permissions.size());
        if( request_permissions.size() > 0 ) {
            ActivityCompat.requestPermissions(this, (String[]) request_permissions.toArray(new String[request_permissions.size()]), 123 );
            //String [] a = new String[]{ Manifest.permission.READ_PHONE_STATE };
            //ActivityCompat.requestPermissions(this, a, 123 );
        }

        return request_permissions.size();
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
