package com.purehero.module.appcompattabactivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTabHost;

/**
 * Created by MY on 2017-02-25.
 */

public class AppCompatTabViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Object> fragmentList = new ArrayList<Object>();
    private List<String> fragmentName = new ArrayList<String>();
    private final MaterialTabHost tabHost;

    public AppCompatTabViewPagerAdapter(FragmentManager fm, MaterialTabHost tabHost ) {
        super(fm);
        this.tabHost = tabHost;
    }

    public void addItem( Fragment fragment, int title_res_id ) {
        String title = tabHost.getContext().getString( title_res_id );
        addItem( fragment, title );
    }

    public void addItem( Fragment fragment, String title ) {
        fragmentList.add( fragment );
        fragmentName.add( title );
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
            tabHost.setSelectedNavigationItem(position);
        }
    };

    /**
     * back 버튼 클릭 시 각 fragment 에서 처리 할수 있도록 함수를 호출한다.
     *
     * @param index
     * @return
     */
    public boolean onBackPressed( int index ) {
        return ((AppCompatTabFragment)fragmentList.get(index)).onBackPressed();
    }

    public void removeItem(int removeItem) {
        fragmentList.remove( removeItem );
        fragmentName.remove( removeItem );
    }
}
