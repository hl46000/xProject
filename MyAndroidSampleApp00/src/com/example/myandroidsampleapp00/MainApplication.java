package com.example.myandroidsampleapp00;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class MainApplication extends Application {
	final String LOG_TAG = "TEST_APPLICATION";
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		
		new Handler().postDelayed( new Runnable(){

			@Override
			public void run() {
				MainApplication.this.startService( new Intent( MainApplication.this, MainService.class ));
				
			}},  1000 );
		
		Log.d( LOG_TAG, "attachBaseContext");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d( LOG_TAG, "onCreate");
	}

	@Override
	public void onLowMemory() {
		Log.d( LOG_TAG, "onLowMemory");
		
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		Log.d( LOG_TAG, "onTerminate");
		
		super.onTerminate();		
	}

	@Override
	public void onTrimMemory(int level) {
		Log.d( LOG_TAG, "onTrimMemory");
		
		super.onTrimMemory(level);
	}

}
