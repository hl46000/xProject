package com.purehero.module.tabhost;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.purehero.module.common.R;

import java.io.IOException;

/**
 * Created by purehero on 2017-03-27.
 */

public abstract class TabAppCompatActivity extends AppCompatActivity {
    private TabLayout tabHost;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;

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
    public void addTab( FragmentEx fragment, String title ) {
        pagerAdapter.addItem( fragment, title );
        pagerAdapter.notifyDataSetChanged();
    }

    public void removeTab( int tabIndex ) {
        pagerAdapter.removeItem( tabIndex );
        pagerAdapter.notifyDataSetChanged();
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
