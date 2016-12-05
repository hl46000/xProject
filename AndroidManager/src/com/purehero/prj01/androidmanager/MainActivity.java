package com.purehero.prj01.androidmanager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private ListView apkListView 			= null;
	private ProgressBar progressBar  		= null;
	private ApkListAdapter apkListAdapter 	= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
						
		new Thread( apk_info_load_runnable ).start();
	}

	Runnable apk_info_load_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// 초기화면을 표시하기 위해 필요한 데이터 수집
			getApkInfos();
			
			// 수집된 데이터 화면에 보여 주기
			runOnUiThread( init_ui_runnable );
		}
	};
	
	Runnable init_ui_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// Progress bar 사라지게 하기
			progressBar = ( ProgressBar ) findViewById( R.id.progressBar );
			progressBar.setVisibility( View.GONE );
			progressBar = null;
			
			// ListView 나타나게 하기
			apkListView	= ( ListView ) findViewById( R.id.apkListView );
			apkListView.setVisibility( View.VISIBLE );
			apkListView.setAdapter( apkListAdapter );			
			registerForContextMenu( apkListView );
		}
	};
	
	
	/**
	 * 설치된 앱(APK)들의 정보(아이콘, 앱이름, 패키지명)등을 추출한다. 
	 */
	private void getApkInfos() 
	{
	    PackageManager pm =  getPackageManager();
	    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
	    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		    
	    List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
	    
	    apkListAdapter = new ApkListAdapter( MainActivity.this );
	    
	    for(int i=0; i<homeApps.size(); i++){
	        ResolveInfo info = homeApps.get(i);
	        apkListAdapter.addItem( 
	        	info.loadIcon(pm),
	        	(String) info.loadLabel(pm), 
	        	info.activityInfo.packageName 
	        );
	    }	
	    
	    apkListAdapter.sort();
	}
			
	// Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
	private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
	private long backPressedTime = 0;
	
	@Override
	public void onBackPressed() 
	{
		if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
			super.onBackPressed();
			
		} else {
			backPressedTime = System.currentTimeMillis();
			
			Toast.makeText( this, "뒤로 버튼을 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT ).show();;
		}
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		if ( v.getId() == R.id.apkListView ) {
			String[] menuItems = getResources().getStringArray(R.array.ApkMenu);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ApkListData data = ( ApkListData ) apkListAdapter.getItem( info.position );
		
		Toast.makeText( MainActivity.this, data.appName + " " + item.getTitle(), Toast.LENGTH_SHORT ).show();
			
		return true;
	}
}
