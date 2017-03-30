package com.purehero.quick.lotto.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.zxing.client.android.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView bannerAdView 			= null; // 하단 배너 광고
    private InterstitialAd interstitialAd	= null;	// 전면 광고

    private ProgressBar progressBar = null;
    private WebView mWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult( new Intent( MainActivity.this, CaptureActivity.class ), 100 );
            }
        });

        progressBar = ( ProgressBar ) 	findViewById( R.id.progressBar );

        init_webview();
        openUrl( "http://m.nlotto.co.kr/" );

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

        checkPermission();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

        if( arg2 != null ) {
            String contents = arg2.getStringExtra( "CONTENTS" );
            if( contents == null ) {
                //Log.d( "LottoScaner", String.format( "Activity Result NULL" ));
            } else {
                // 번호 확인 URL로 시작하는 데이터 이면
                openUrl( contents );// 함수를 이용해서 화면에 표시 해 준다.
                //Log.d( "LottoScaner", String.format( "Activity Result : %s", contents ));
            }
        }

        showFullAd();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if( requestCode == 123 ) {
            boolean retry = false;
            for( int grant : grantResults ) {
                if( PackageManager.PERMISSION_GRANTED != grant ) {
                    retry = true;
                    break;
                }
            }

            if( retry ) {
                checkPermission();
            }
        }
    }

    private void checkPermission() {
        String permissions[] = {
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        List<String> request_permissions = new ArrayList<String>();
        for( String permission : permissions ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }
        if( request_permissions.size() > 0 ) {
            ActivityCompat.requestPermissions(this, permissions, 123 );
        }
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
                Toast.makeText(MainActivity.this, "로딩오류",
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnScan) {
            startActivityForResult( new Intent( MainActivity.this, CaptureActivity.class ), 100 );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showFullAd() {
        if( interstitialAd != null ) {
            if( interstitialAd.isLoaded()) {
                if( System.currentTimeMillis() % 20 == 3 ) {
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
