package com.purehero.module.appcompattabactivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
}
