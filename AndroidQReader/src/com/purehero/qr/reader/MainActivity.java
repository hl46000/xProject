package com.purehero.qr.reader;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements MaterialTabListener {

	private MaterialTabHost tabHost;
	private ViewPager pager;
    private ViewPagerAdapter adapter;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );
        
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        addTabs( adapter );
        
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
        
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab( tabHost.newTab().setText(adapter.getPageTitle(i)).setTabListener(this));
        }
	}

	/**
	 * Tab에 들어갈 Fragment 을 생성하여 adapter에 등록한다. 
	 * 
	 * @param adapter
	 */
	private void addTabs( ViewPagerAdapter adapter ) {
		adapter.addFragment( new FragmentText("테스트 1"), "Tab1" );
		adapter.addFragment( new FragmentText("테스트 2"), "Tab2" );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* 
	 * 단말기 가로/세로 전환시 데이터 재 로딩 방지
	 */
	Configuration oldConfig = null;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	
	private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
	private long backPressedTime = 0;
		
	@Override
	public void onBackPressed() 
	{
		if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
			super.onBackPressed();
			
		} else {
			backPressedTime = System.currentTimeMillis();
			Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
		}
	}
	
	@Override
	public void onTabSelected(MaterialTab tab) {
		pager.setCurrentItem( tab.getPosition());
	}

	@Override
	public void onTabReselected(MaterialTab tab) {
	}

	@Override
	public void onTabUnselected(MaterialTab tab) {
	}
}
