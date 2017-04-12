package com.purehero.module.tabhost;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.purehero.module.fragment.FragmentEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017-02-25.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter implements TabLayout.OnTabSelectedListener {
    private List<Object> fragmentList = new ArrayList<Object>();
    private List<String> fragmentName = new ArrayList<String>();
    private final TabLayout tabHost;
    private final ViewPager pager;

    public ViewPagerAdapter( FragmentManager fm, TabLayout tabHost, ViewPager pager) {
        super(fm);
        this.tabHost = tabHost;
        this.pager = pager;

        pager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabHost ));
        tabHost.setOnTabSelectedListener( this );
    }

    public void addItem( Fragment fragment, int title_res_id ) {
        String title = tabHost.getContext().getString( title_res_id );
        addItem( fragment, title );
    }

    public void addItem( Fragment fragment, String title ) {
        fragmentList.add( fragment );
        fragmentName.add( title );

        tabHost.addTab(
                tabHost.newTab()
                        .setText(title)
                        //.setTabListener(this)
        );
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //tabHost.notifyDataSetChanged();
    }

    public Fragment getItem(int position) {
        return ( Fragment ) fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentName.get(position);
    }

    public ViewPager.SimpleOnPageChangeListener listener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            //tabHost..setSelectedNavigationItem(position);
        }
    };

    /**
     * back 버튼 클릭 시 각 fragment 에서 처리 할수 있도록 함수를 호출한다.
     *
     * @param index
     * @return
     */
    public boolean onBackPressed( int index ) {
        return ((FragmentEx)fragmentList.get(index)).onBackPressed();
    }

    public void removeItem(int removeItem) {
        tabHost.removeTabAt( removeItem );
        fragmentList.remove( removeItem );
        fragmentName.remove( removeItem );
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
