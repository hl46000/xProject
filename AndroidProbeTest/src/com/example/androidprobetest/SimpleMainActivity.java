package com.example.androidprobetest;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import com.covault.appsec.CovaultMessagePanel;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SimpleMainActivity extends ProbeMainActivity implements OnClickListener {
	public native void Init(Context ctx);
	public native void BuyItem();
	public native void SellItem();
	public native int GetMoney();
	public native long GetTimeOfDay();
	public native long GetTimeAfterBoot();
	
	public native void CheckInjection();
	public native void Hooking();
		
	static{
		System.loadLibrary("MainSimple");
		System.loadLibrary("MainHooking");
		System.loadLibrary("MainInjection");
	}
	
	private long m_longTime;
	private long m_bootTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_activity_main);
								
		Init( this );
		
		// set button
		int btnIDs[] = { R.id.btnBuyItem, R.id.btnSellItem, R.id.btnCheck, R.id.btnHooking, R.id.btnPopup };
		for( int btnID : btnIDs ) {
			Button btn = ( Button ) findViewById( btnID );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		UpdateGameInfo();
		
		String strUUIDseed = "test uuid seed value";
		Log.d( "SampleApp", String.format( "JAVA UUID : %s( %s )", UUID.nameUUIDFromBytes( strUUIDseed.getBytes() ).toString(), strUUIDseed ));
		
		
		Log.d( "SampleApp", String.format( "getExternalFilesDir : %s", this.getExternalFilesDir(null).getAbsolutePath() ));
		Log.d( "SampleApp", String.format( "getExternalStorageDirectory : %s", Environment.getExternalStorageDirectory().getAbsolutePath() ));
		
		String internal_sd_path = System.getenv( "EXTERNAL_STORAGE" );
		String secondary_sd_path = System.getenv( "SECONDARY_STORAGE" );
		String external_sd_path = System.getenv( "EXTERNAL_SDCARD_STORAGE" );
		
		Log.d( "SampleApp", "EXTERNAL_STORAGE : " + internal_sd_path );
		Log.d( "SampleApp", "SECONDARY_STORAGE : " + secondary_sd_path );
		Log.d( "SampleApp", "EXTERNAL_SDCARD_STORAGE : " + external_sd_path );
		
		new Thread( new Runnable(){
			@Override
			public void run() {
				
				while( true ) {
					m_longTime = GetTimeOfDay();
					m_bootTime = GetTimeAfterBoot();
							
					SimpleMainActivity.this.runOnUiThread( new Runnable(){
	
						@Override
						public void run() {
							TextView textView = (TextView) SimpleMainActivity.this.findViewById(R.id.textView1);
							if( textView != null ) {
								textView.setText( new Date( m_longTime * 1000 ).toLocaleString() );
							}
							
							textView = (TextView) SimpleMainActivity.this.findViewById(R.id.textView2 );
							if( textView != null ) {
								textView.setText( new Date( m_bootTime * 1000 ).toLocaleString() );
							}
						}});
				}
				
			}} ).start();
		
	}
	
	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnBuyItem :
			BuyItem();
			UpdateGameInfo();
			break;
		case R.id.btnSellItem :
			SellItem();
			UpdateGameInfo();
			break;
			
		case R.id.btnCheck :
			CheckInjection();
			break;
			
		case R.id.btnHooking :
			Hooking();
			break;
			
		case R.id.btnPopup :
			showAlertPanel( "haha hoho" );			
			break;
		}
		
	}	
	
	public void UpdateGameInfo() {
		TextView moneyTextView = ( TextView ) findViewById( R.id.txtMoneyVal );
		if( moneyTextView != null ) {
			String strMoney;
			DecimalFormat df = new DecimalFormat("#,##0");
	
			// update money text
			strMoney = df.format(GetMoney());
			moneyTextView.setText(strMoney);
		}
	}
	
	/**
	 * @param msg
	 */
	public void showAlertPanel( String msg ) {
		Log.d( "SampleApp", String.format( "showAlertPanel : %s", msg ));
		
		CovaultMessagePanel alert = new CovaultMessagePanel( SimpleMainActivity.this );
		alert.init( " Notice" );
		alert.showPanel( CovaultMessagePanel.HACKING_ALERT , msg );
	}
}