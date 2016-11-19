package com.example.appsealingexternalexecutor;

import com.example.service.AppSealingService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceTestActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int btnIDs[] = { R.id.btnLauncherActivity, R.id.btnNoticeActivity, R.id.btnRepeatNotification, R.id.btnSecondActivity };
		for( int id : btnIDs ) {
			Button btn = ( Button ) this.findViewById( id );
			if( btn == null ) continue;
			
			btn.setOnClickListener( this );
		}

		startServiceTest();
	}
	
	private void startServiceTest() {
		Intent startMyService= new Intent();
		startMyService.setClass( this, AppSealingService.class );
		startMyService.setFlags( ServiceInfo.FLAG_STOP_WITH_TASK );
		startService(startMyService);
	}

	private boolean backgroundPersistenceFlag = false;
	private Runnable startServiceRunnable = new Runnable() {
		@Override
		public void run() {
			ServiceTestActivity.this.startTestService( ServiceTestActivity.this, backgroundPersistenceFlag );
			backgroundPersistenceFlag = !backgroundPersistenceFlag;
			
			if( bNotificationRepeat ) {
				new Handler().postDelayed( startServiceRunnable, 5000 );
			}
		}
	};
	
	/**
	 * 
	 * 
	 * @param context
	 * @param BackgroundPersistenceFlag
	 */
	private void startTestService( Context context, boolean BackgroundPersistenceFlag ) {
		startTestService( context, BackgroundPersistenceFlag, true );
	}
	
	private void startTestService( Context context, boolean BackgroundPersistenceFlag, boolean bLauncherActivity ) {
		Intent startMyService= new Intent();
		startMyService.setClass( context, AppSealingService.class );
		startMyService.setFlags( ServiceInfo.FLAG_STOP_WITH_TASK );
		startMyService.putExtra("probe_service_background_persistence", BackgroundPersistenceFlag ? "false" : "true" );
		startMyService.putExtra( "bLauncherActivity", bLauncherActivity );
		context.startService(startMyService);
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnLauncherActivity 	: startTestService( this, false, true ); break;
		case R.id.btnNoticeActivity 	: startTestService( this, false, false ); break;
		case R.id.btnRepeatNotification	: 
			if( !bNotificationRepeat ) {
				new Handler().postDelayed( startServiceRunnable, 5000 );
			} 
			bNotificationRepeat = !bNotificationRepeat;
			break;
			
		case R.id.btnSecondActivity : OnClickGoSecondActivity(); break;
		}
	}
	
	private void OnClickGoSecondActivity() {
		try {
			Class<?> cls = Class.forName( "com.example.appsealingexternalexecutor.SecondActivity" );
			
			Intent startIntent = new Intent();
			startIntent.setClass( this, cls );
			startActivity(startIntent);
			finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean bNotificationRepeat = false;
}
