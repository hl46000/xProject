package com.example.appsealingexternalexecutor;

import com.example.run.executor.ExternalExecutor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Bundle;
import android.util.Log;

public class ExternalExecutorActivity extends Activity {
	static {
		System.loadLibrary( "AppSealingExternalExecutor" );
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		externalExecutorTest();
	}
	
	/**
	 * 외부 실행 파일 테스트 
	 */
	private void externalExecutorTest() {
		ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
		for( RunningAppProcessInfo runningProcessInfo : actvityManager.getRunningAppProcesses()){
	        Log.d("AppSealing_Main", String.format( "%s[%d]", runningProcessInfo.processName, runningProcessInfo.pid ));
		}
		
		ExternalExecutor executor = new ExternalExecutor(this);
		int result = 0;
		try {
			result = executor.executeFileFromAssets("attacher", new String[]{ "123123","adf" } );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d( "AppSealing_Main", String.format( "attacher return value : %d", result));
	}

}
