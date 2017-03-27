package com.purehero.module.tabhost;

import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.purehero.module.common.R;

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
}
