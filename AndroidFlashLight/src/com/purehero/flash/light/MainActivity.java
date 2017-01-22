package com.purehero.flash.light;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity implements OnClickListener {

	private FlashLight2 flashLight = null;
	private AdView bannerAdView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		int btnIDs [] = new int[]{ R.id.btnFlash };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		flashLight = new FlashLight2( this );
		if( !flashLight.init()) {
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton( AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
		}
		
		// 하단 배너 광고 표시
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
	protected void onDestroy() {
		flashLight.release();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	private boolean flash = false;
	
	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnFlash :
			flash = !flash;
			
			Button btn = ( Button ) arg0;
			btn.setBackgroundResource( flash ? R.drawable.light_on : R.drawable.light_off );
			
			flashLight.setFlashLight( flash );
			break;
		}	
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
}
