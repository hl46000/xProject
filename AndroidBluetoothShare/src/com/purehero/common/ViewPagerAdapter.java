package com.purehero.common;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	private List<Object> fragmentList = new ArrayList<Object>();
    private List<String> fragmentName = new ArrayList<String>();
    
	public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
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
}
