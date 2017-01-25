package com.purehero.lotto.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.zxing.client.android.CaptureActivity;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	private AdView bannerAdView 			= null; // 하단 배너 광고
	private InterstitialAd interstitialAd	= null;	// 전면 광고
	
	private ProgressBar progressBar = null;
	private WebView mWebView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		progressBar = ( ProgressBar ) 	findViewById( R.id.progressBar );
		
		int btnIDs [] = new int[]{ R.id.btnMenu01, R.id.btnMenu02, R.id.btnMenu03 };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		
		// 하단 배너 광고 표시
 		bannerAdView = (AdView) findViewById(R.id.adView);
 		if( bannerAdView != null ) {
 			AdRequest adRequest = new AdRequest.Builder().build();
 			bannerAdView.setAdListener( new AdListener(){
 				@Override
 				public void onAdLoaded() {
 					super.onAdLoaded();
 					bannerAdView.setVisibility( View.VISIBLE );
 				}});
 			
 			bannerAdView.loadAd(adRequest);
 		}
 		 		
 		init_webview();
 		openUrl( "http://m.nlotto.co.kr/" );	    
	}

	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		
		if( arg2 != null ) {
			String contents = arg2.getStringExtra( "CONTENTS" );
			if( contents == null ) {
				Log.d( "LottoScaner", String.format( "Activity Result NULL" ));
			} else {
				// 번호 확인 URL로 시작하는 데이터 이면
				openUrl( contents );// 함수를 이용해서 화면에 표시 해 준다.
				Log.d( "LottoScaner", String.format( "Activity Result : %s", contents ));
			}
		}
		
		showFullAd();
	}


	private void init_webview() {
		mWebView 	= ( WebView ) 		findViewById( R.id.webView );
		if( mWebView == null ) return;
		
		WebSettings webSettings = mWebView.getSettings();
	    webSettings.setJavaScriptEnabled(true);
	    
	    mWebView.setWebViewClient(new WebViewClient() {
			/*
	    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			};
			*/
			
			public void onPageStarted(WebView view, String url,
					android.graphics.Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
			};

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
			};
			
			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				Toast.makeText(MainActivity.this, "로딩오류" + error.getDescription(),
						Toast.LENGTH_SHORT).show();
				
				super.onReceivedError(view, request, error);
			}
		});
	}
	
	private void openUrl(String url ) {
		if( mWebView == null ) return;
		
		if( progressBar != null ) {
			progressBar.setVisibility( View.VISIBLE );
		}
		
		mWebView.loadUrl( url );
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
	public void onBackPressed() {
		WebBackForwardList list = mWebView.copyBackForwardList();
		
		if( list.getCurrentIndex() > 0 && mWebView.canGoBack()) {
			mWebView.goBackOrForward( -1 );
			
		} else {		
			if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
				super.onBackPressed();
				
			} else {
				backPressedTime = System.currentTimeMillis();
				Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
			}
		}
	}
	
	public void showFullAd() {
		if( interstitialAd != null ) {
			if( interstitialAd.isLoaded()) {
				if( System.currentTimeMillis() % 10 < 3 ) 
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



	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnMenu01 : openUrl( "http://m.nlotto.co.kr/gameResult.do?method=byWin" ); break; 
		case R.id.btnMenu02 : openUrl( "http://m.nlotto.co.kr/store.do?method=topStore&pageGubun=L645" ); break; 
		case R.id.btnMenu03 : openUrl( "http://m.nlotto.co.kr/gameResult.do?method=statByNumber" ); break;	 
		}
	}
}
