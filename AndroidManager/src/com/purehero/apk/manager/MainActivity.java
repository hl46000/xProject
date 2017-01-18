package com.purehero.apk.manager;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends ActionBarActivity implements MaterialTabListener
{
	private MaterialTabHost tabHost;
	private ViewPager pager;
    private ViewPagerAdapter adapter;
    
    private List<Object> fragmentList = new ArrayList<Object>();
    private List<String>   fragmentName = new ArrayList<String>();
    
    private InterstitialAd interstitialAd	= null;	// 전면 광고
    private AdView bannerAdView = null;
    private boolean activity_result = false;
	private int resume_cnt = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );

        fragmentList.add( new ApkListFragment( this )); fragmentName.add( "Apps" );
        fragmentList.add( new FileListFragment( this )); fragmentName.add( "Files" );
        fragmentList.add( new ApkHistoryFragment( this )); fragmentName.add( "History" );
        
        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }
		
        bannerAdView = (AdView) findViewById(R.id.adView);
        if( bannerAdView != null ) {
			bannerAdView.setVisibility( View.GONE );
			
			AdRequest adRequest = new AdRequest.Builder().build();
			bannerAdView.setAdListener( new AdListener(){
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
					bannerAdView.setVisibility( View.VISIBLE );
				}});
			
			bannerAdView.loadAd(adRequest);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if( activity_result ) {
			resume_cnt ++;
			
			if( resume_cnt > 1 ) {
				showFullAd();
				
				activity_result = false;
				resume_cnt		= 0;
			}
		}		
		G.log( "MainActivity::onResume " + resume_cnt );
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent ) {
		super.onActivityResult( requestCode, resultCode, intent );
	
		activity_result = true;
		G.log( "MainActivity::onActivityResult");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int tabIdx = pager.getCurrentItem();
		String tabTitle = fragmentName.get( tabIdx );
		
		G.log( "tabTitle : " + tabTitle);
		menu.findItem( R.id.delete_all_items ).setVisible( tabTitle.compareTo( "History" ) == 0 );
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch( id ) {
		case R.id.action_settings :
			startActivity( new Intent( this, SettingsActivity.class ));
			return true;
			
		case R.id.delete_all_items :
			int tabIdx = pager.getCurrentItem();
			ApkHistoryFragment fragment = ( ApkHistoryFragment ) fragmentList.get( tabIdx );
			fragment.removeAllItems();
			break;
		} 
		return super.onOptionsItemSelected(item);
	}
	
	// Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
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
	
	private class ViewPagerAdapter extends FragmentStatePagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
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
	
	public void showFullAd() {
		G.log( "showFullAd" );
		
		if( interstitialAd != null ) {
			if( interstitialAd.isLoaded()) {
				if( System.currentTimeMillis() % 10 < 3 ) {
					interstitialAd.show();
				}
			}
			return;
		}
		
		interstitialAd = new InterstitialAd( this );
		interstitialAd.setAdUnitId( getResources().getString(R.string.ad_unit_id) );
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitialAd.loadAd(adRequest);
		interstitialAd.setAdListener( new AdListener(){
			
			@Override
			public void onAdFailedToLoad(int errorCode) {
			}

			@Override
			public void onAdLoaded() {
			}

			@Override
			public void onAdClosed() {
				AdRequest adRequest = new AdRequest.Builder().build();
				interstitialAd.loadAd(adRequest);
			}
		});
	}
}
