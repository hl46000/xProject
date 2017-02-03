package com.purehero.common;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import it.neokree.materialtabs.MaterialTabHost;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	private List<Object> fragmentList = new ArrayList<Object>();
    private List<String> fragmentName = new ArrayList<String>();
    private final MaterialTabHost tabHost;
    
	public ViewPagerAdapter(FragmentManager fm, MaterialTabHost tabHost ) {
        super(fm);
        this.tabHost = tabHost;
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
}
