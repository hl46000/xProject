package com.purehero.lotto.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.client.android.CaptureActivity;

public class MainActivity extends ActionBarActivity {
	
	private AdView bannerAdView = null;
	
	private ProgressBar progressBar = null;
	private WebView webView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		progressBar = ( ProgressBar ) 	findViewById( R.id.progressBar );
		webView 	= ( WebView ) 		findViewById( R.id.webView );
		
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
 		
 		
 		if( webView != null ) {
 			WebSettings webSettings = webView.getSettings();
		    webSettings.setJavaScriptEnabled(true);
		    
 			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				};
				
				public void onPageStarted(WebView view, String url,
						android.graphics.Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					progressBar.setVisibility(View.VISIBLE);
				};

				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					progressBar.setVisibility(View.INVISIBLE);
				};

				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					Toast.makeText(MainActivity.this, "로딩오류" + description,
							Toast.LENGTH_SHORT).show();
				};
				
			});
						 
		    
 		}
	 
 		openUrl( "http://m.nlotto.co.kr/" );	    
	}

	private void openUrl(String url ) {
		if( webView == null ) return;
		
		if( progressBar != null ) {
			progressBar.setVisibility( View.VISIBLE );
		}
		
		webView.loadUrl( url );
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
		if (id == R.id.btnScan ) {
			startActivityForResult( new Intent( MainActivity.this, CaptureActivity.class ), 100 );
			return true;
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
}
