package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppSealingForegroundService extends Service {
	public static final int NOTIFICATION_ID_FOR_APPSEALING_SERVICE = 9058;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		startService( new Intent( this, AppSealingService.class));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		AppSealingService.instance.onStartCommand(intent, flags, startId);
		startForeground( NOTIFICATION_ID_FOR_APPSEALING_SERVICE, AppSealingService.instance.getNotification(NOTIFICATION_ID_FOR_APPSEALING_SERVICE,null,"","",""));
		
		stopForeground(true);
		stopSelf();
		
		return START_NOT_STICKY;
	}
	
	
}
