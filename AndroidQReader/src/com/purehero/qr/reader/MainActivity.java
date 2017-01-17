package com.purehero.qr.reader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.zxing.client.android.ViewfinderView;

public class MainActivity extends com.google.zxing.client.android.CaptureActivity {
    public static MainActivity instance = null;
    
	private InterstitialAd interstitialAd	= null;	// 전면 광고
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
	    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    resultView = findViewById(R.id.result_view);
	    statusView = (TextView) findViewById(R.id.status_view);
	    
	    // FIXME : 앱을 배포하기 전까진 풀지 말것
	    AdView mAdView = (AdView) findViewById(R.id.adView);
	    //AdView mAdView = null; 
		if( mAdView != null ) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
		}
		
		this.byPassHandler = result_handler;
		
		instance = this;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( requestCode == 100 ) {
			resetStatusView();
		}
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

	/**
	 * @param message
	 */
	private Handler result_handler = new Handler() {
		public void handleMessage(Message message) {
			Intent intent = new Intent( MainActivity.this, ResultActivity.class );
			
			switch (message.what) {
		    case R.id.return_scan_result:
		    	Intent data = (Intent) message.obj;
		    	intent.putExtra( "title", data.getStringExtra("TITLE") );
		    	//intent.putExtra( "content", data.getStringExtra("SCAN_RESULT"));
		    	intent.putExtra( "content", data.getStringExtra("CONTENTS"));
		    	intent.putExtra( "format", data.getStringExtra( "SCAN_RESULT_FORMAT") );
		    	break;
		    case R.id.launch_product_query:
		    	intent.putExtra( "title", "URI" );
		    	intent.putExtra( "content", (String) message.obj );		    	
		    	break;
		    }
			
			
			MainActivity.this.startActivityForResult( intent, 100 );
		    
		}
	};
	
	private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
	private long backPressedTime = 0;
		
	@Override
	public void onBackPressed() {
		if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
			super.onBackPressed();
			
		} else {
			backPressedTime = System.currentTimeMillis();
			Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
		}
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
