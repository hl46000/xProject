package com.purehero.root.checker;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends ActionBarActivity implements MaterialTabListener {
	private final String LOG_TAG = "RootChecker";
	
	private MaterialTabHost tabHost;
	private ViewPager pager;
    private ViewPagerAdapter adapter;
    
    private List<Object> fragmentList = new ArrayList<Object>();
    private List<String>   fragmentName = new ArrayList<String>();
	
    private InterstitialAd interstitialAd	= null;	// 전면 광고
    private AdView bannerAdView = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d( LOG_TAG, "onCreate" );
		
		tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );
        
		fragmentList.add( new DeviceInfoFragment( this )); fragmentName.add( "Info" );
        fragmentList.add( new CheckRootFragment( this )); fragmentName.add( "Check root" );
		
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
		showFullAd();
	}
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
	
	private class ViewPagerAdapter extends FragmentStatePagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
        	//Log.d( LOG_TAG, "ViewPagerAdapter::getItem" );
        	
            return ( Fragment ) fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	//Log.d( LOG_TAG, "ViewPagerAdapter::getPageTitle" );
        	return fragmentName.get(position);
        }
    }

	@Override
	public void onTabSelected(MaterialTab tab) {
		//Log.d( LOG_TAG, "onTabSelected" );
		
		pager.setCurrentItem( tab.getPosition());
	}

	@Override
	public void onTabReselected(MaterialTab tab) {
		//Log.d( LOG_TAG, "onTabReselected" );
	}

	@Override
	public void onTabUnselected(MaterialTab tab) {
		//Log.d( LOG_TAG, "onTabUnselected" );
		
		// 전환 시 너무 자주 광고를 표시하지 안게 하기 위해 추가한다.  
		showFullAd();		
	}	
	
	public void showFullAd() {
		//Log.d( LOG_TAG, "showFullAd" );
		
		if( interstitialAd != null ) {
			if( interstitialAd.isLoaded()) {
				if( System.currentTimeMillis() % 10 < 2 ) 
				{
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
