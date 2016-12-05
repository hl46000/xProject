package com.purehero.prj01.androidmanager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ListView apkListView = null;
	private ApkListAdapter apkListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		apkListAdapter = new ApkListAdapter( this );
		getApkInfos();
		
		apkListView = ( ListView ) findViewById( R.id.apkListView );
		apkListView.setAdapter( apkListAdapter );
	}

	/**
	 * 설치된 앱(APK)들의 정보(아이콘, 앱이름, 패키지명)등을 추출한다. 
	 */
	private void getApkInfos() 
	{
	    PackageManager pm =  getPackageManager();
	    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
	    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		    
	    List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
	    
	    for(int i=0; i<homeApps.size(); i++){
	        ResolveInfo info = homeApps.get(i);
	        apkListAdapter.addItem( info.loadIcon(pm), (String) info.loadLabel(pm), info.activityInfo.packageName );
	    }	  
	}

	private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
	private long backPressedTime = 0;
	
	@Override
	public void onBackPressed() {
		if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
			super.onBackPressed();
			
		} else {
			backPressedTime = System.currentTimeMillis();
			
			Toast.makeText( this, "뒤로 버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT ).show();;
		}
	}
	
	
}
