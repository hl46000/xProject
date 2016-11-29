package com.example.apk.extractor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.appsealingexternalexecutor.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ApkExtractorActivity extends Activity implements OnClickListener {
	final String LOG_TAG = "TEST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.extractor_main );

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	// 세로 모드
		/*
		Map<String,String> infos = AndroidDeviceInfo.getInfo( this );
		Set<String> keys = infos.keySet();
		for( String key : keys ) {
			Log.i( LOG_TAG, String.format( "%s = %s", key, infos.get( key )));
		}
		getSensorInfo();
		*/
		
		int btn_ids [] = { R.id.btnSetting };
		for( int id : btn_ids ) {
			Button btn = ( Button ) this.findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		String names[] = getHomeLauncher();
		for( String name : names ) {
			Log.i( LOG_TAG, name );
		}
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

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnSetting : 
			startActivity( new Intent( this, SettingActivity.class ) );
			break;
		}
	}
	
	private String[] getHomeLauncher(){
	    String[] HomeLauncher;
	    PackageManager pm =  getPackageManager();
	    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
	    //homeIntent.addCategory(Intent.CATEGORY_HOME);
	    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	    
	    List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
	    HomeLauncher = new String[homeApps.size()];
	    for(int i=0; i<homeApps.size(); i++){
	        ResolveInfo info = homeApps.get(i);
	        //HomeLauncher[i] = info.activityInfo.packageName;
	        HomeLauncher[i] = (String) info.loadLabel(pm);
	    }
	    return HomeLauncher;
	}
}
