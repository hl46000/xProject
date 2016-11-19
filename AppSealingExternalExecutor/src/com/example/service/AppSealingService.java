package com.example.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AppSealingService extends Service {
	public static AppSealingService instance;
	
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		Log.d( "AppSealingService", "onStartCommand");
		return _onStartCommand( intent, flags, startId );
		
	}
	
	@Override
	public void onLowMemory() {
		Log.d( "AppSealingService", "onLowMemory()" );
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		Log.d( "AppSealingService", String.format( "onTrimMemory(%d)", level ));
		super.onTrimMemory(level);
	}

	/**
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 */
	private int _onStartCommand( Intent intent, int flags, int startId ) {
		String probe_service_background_persistence = intent.getStringExtra( "probe_service_background_persistence" );
		if( probe_service_background_persistence == null ) return START_NOT_STICKY;
		
		boolean backgroundPersistenceFlag = Boolean.valueOf( probe_service_background_persistence );
		boolean bLauncherActivity = intent.getBooleanExtra( "bLauncherActivity", true );
		
		final int NOTIFICATION_ID = AppSealingForegroundService.NOTIFICATION_ID_FOR_APPSEALING_SERVICE;
		final String notificationTitle = "Notification TEST";		
		final String notificationExpandedMessage = "This notification indicates that [test] is being secured by AppSealing Mobile Game Protection.";
		final String notificationSummary = "www.appsealing.com";
		
		if( backgroundPersistenceFlag ) {
			if( android.os.Build.VERSION.SDK_INT > 10 ) {
				Notification notification = getNotification( NOTIFICATION_ID, 
						bLauncherActivity ? getLauncherActivityIntent() : getActivityIntent(),
						notificationTitle, notificationExpandedMessage, notificationSummary );
				
				startForeground( NOTIFICATION_ID, notification );				
			} else {
				startForeground( NOTIFICATION_ID, new Notification());
			}
		} else {
			stopForeground( true );
		}
		
		Log.d( "AppSealingService", String.format( "onStartCommand : probe_service_background_persistence[%s][%s]", probe_service_background_persistence, bLauncherActivity?"true":"false" ));
				
		return START_NOT_STICKY;
	}
	
	
	
	/**
	 * @param NOTIFICATION_ID
	 * @param notificationTitle
	 * @param notificationExpandedMessage
	 * @param notificationSummary
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Notification getNotification( int NOTIFICATION_ID, Intent activityIntent, String notificationTitle, String notificationExpandedMessage, String notificationSummary  ) {
		Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());			
		mBuilder.setContentTitle( notificationTitle );	// 알림 제목
		mBuilder.setContentText(notificationExpandedMessage);
		
		//startForeground( NOTIFICATION_ID, (android.os.Build.VERSION.SDK_INT >= 16)? mBuilder.build() : mBuilder.getNotification());
		
		if(android.os.Build.VERSION.SDK_INT >= 16) {
			mBuilder.setStyle(new Notification.BigTextStyle().setSummaryText( notificationSummary ).bigText( notificationExpandedMessage ));
			mBuilder.setPriority( Notification.PRIORITY_MAX );
		}
		
		if( activityIntent != null ) {
			PendingIntent pendingIntent = PendingIntent.getActivity( AppSealingService.this, NOTIFICATION_ID, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT );
			mBuilder.setContentIntent(pendingIntent);	// 알림 터치시 반응
		}
		mBuilder.setAutoCancel(false);	// 알림 터치시 반응 후 알림 삭제 여부
		mBuilder.setSmallIcon( AppSealingService.this.getApplicationInfo().icon );
		
		return android.os.Build.VERSION.SDK_INT >= 16 ? mBuilder.build() : mBuilder.getNotification();
	}
	
	/**
	 * Activity 을 실행 시킬 Intent 을 반환 합니다. 
	 * 
	 * @return
	 */
	private Intent getActivityIntent() {
		Log.d( "AppSealingService", "AppSealingNoticeActivity");
		Intent startIntent = new Intent(this, AppSealingNoticeActivity.class );
		startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return startIntent;
	}
	
	private Intent getLauncherActivityIntent() {
		String packageName = getApplicationContext().getPackageName();
		Log.d( "AppSealingService", "getLauncherActivityIntent");
		Log.d( "AppSealingService", "package name : " + packageName );
		
		Intent startIntent = this.getPackageManager().getLaunchIntentForPackage( packageName );
		return startIntent;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();
		
		Log.d( "AppSealingService", String.format( "onCreate" ));
		instance = this;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d( "AppSealingService", String.format( "onDestroy" ));
		instance = null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d( "AppSealingService", String.format( "onBind" ));
		
		return null; 
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		
		Log.d( "AppSealingService", String.format( "onTaskRemoved" ));
	}
	
	private int onStartCommand3( Intent intent, int flags, int startId ) {
		String probe_service_background_persistence = intent.getStringExtra( "probe_service_background_persistence" );
		boolean backgroundPersistenceFlag = Boolean.valueOf( probe_service_background_persistence );
		boolean bLauncherActivity = intent.getBooleanExtra( "bLauncherActivity", true );
		
		final int NOTIFICATION_ID = AppSealingForegroundService.NOTIFICATION_ID_FOR_APPSEALING_SERVICE;
		final String notificationTitle = "Notification TEST";		
		final String notificationExpandedMessage = "This notification indicates that [%s] is being secured by AppSealing Mobile Game Protection.";
		final String notificationSummary = "www.appsealing.com";
		
		if( backgroundPersistenceFlag ) {
			Notification notification = getNotification( NOTIFICATION_ID, 
					bLauncherActivity ? getLauncherActivityIntent() : getActivityIntent(),
					notificationTitle, notificationExpandedMessage, notificationSummary );
			startForeground( NOTIFICATION_ID, notification);
			
		} else {
			stopForeground( true );
		}
		
		Log.d( "AppSealingService", String.format( "onStartCommand : probe_service_background_persistence[%s][%s]", probe_service_background_persistence, bLauncherActivity?"true":"false" ));
				
		return START_NOT_STICKY;
	}
	
}