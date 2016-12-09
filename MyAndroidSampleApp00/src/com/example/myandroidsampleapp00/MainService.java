package com.example.myandroidsampleapp00;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	final String LOG_TAG = "TEST_Service";
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d( LOG_TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d( LOG_TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		Log.d( LOG_TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.d( LOG_TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d( LOG_TAG, "onStartCommand");
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onTrimMemory(int level) {
		Log.d( LOG_TAG, "onTrimMemory");
		
		super.onTrimMemory(level);
	}

}
