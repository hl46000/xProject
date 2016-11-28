package com.example.apk.extractor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

public class ApkExtractorActivity extends Activity {
	final String LOG_TAG = "TEST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	// 세로 모드
		
		Map<String,String> infos = AndroidDeviceInfo.getInfo( this );
		Set<String> keys = infos.keySet();
		for( String key : keys ) {
			Log.i( LOG_TAG, String.format( "%s = %s", key, infos.get( key )));
		}
		getSensorInfo();		
	}
	
	private void getSensorInfo() {
		List<Map<String, String>> infos = AndroidSensorInfo.getInfo( this );
		if( infos == null ) return;
		
		for( Map<String, String> sensorInfo : infos ) {
			Set<String> keys = sensorInfo.keySet();
			String name = sensorInfo.get("Name");
			
			Log.i( LOG_TAG, name );
			for( String key : keys ) {
				if( key.compareTo( "Name" ) == 0 ) continue;
				
				String value = sensorInfo.get( key );
				Log.i( LOG_TAG, String.format( "    %s = %s", key, value ));
			}
		}
	}
}
