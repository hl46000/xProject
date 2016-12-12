package com.purehero.prj01.androidmanager;

import java.io.File;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ApkListFragment extends Fragment {
	private ListView apkListView 			= null;
	private ProgressBar progressBar  		= null;
	private ApkListAdapter apkListAdapter 	= null;
	private Stack<ApkListData> workStack 		= new Stack<ApkListData>();
	
	private Activity context = null;
	private View layout = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.apk_list_layout, container, false); 
		
		new Thread( apk_info_load_runnable ).start();
		return layout;	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent ) 
	{
		ApkListData data = null;
		
		if( !workStack.isEmpty()) {
			data = workStack.pop();
		}
		
		switch( requestCode ) {
		case ID_ACTION_APK_UNINSTALL :	
			
			// APK Uninstall 이후 처리 로직 
			// ( resultCode 는 항상 0이다. intent 값은 null 이 넘어 온다. )
			if( data != null ) {
				String packageName = data.getPackageName();
				
				if( packageName != null && !is_apk_installed(packageName)) {
					int position = data.getIndex();
					if( position != -1 ) {
						apkListAdapter.remove( position );
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	Runnable apk_info_load_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// 초기화면을 표시하기 위해 필요한 데이터 수집
			getApkInfos();
			
			// 수집된 데이터 화면에 보여 주기
			context.runOnUiThread( init_ui_runnable );
		}
	};
	
	Runnable init_ui_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// Progress bar 사라지게 하기
			progressBar = ( ProgressBar ) layout.findViewById( R.id.progressBar );
			progressBar.setVisibility( View.GONE );
			progressBar = null;
			
			// ListView 나타나게 하기
			apkListView	= ( ListView ) layout.findViewById( R.id.apkListView );
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
	    apkListAdapter = new ApkListAdapter( context );
	    apkListAdapter.sort();
	}
			
	
		
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		if ( v.getId() == R.id.apkListView ) {
			context.getMenuInflater().inflate(R.menu.contextual, menu);
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ApkListData data = ( ApkListData ) apkListAdapter.getItem( info.position );
		data.setIndex( info.position );
		
		switch( item.getItemId()) {
		case R.id.APK_MENU_RUNNING		: apk_running( data ); 		break;
		case R.id.APK_MENU_GOTO_MARKET	: apk_goto_market( data ); 	break;
		case R.id.APK_MENU_DELETE 		: apk_uninstall( data, info.position ); 	break;
		case R.id.APK_MENU_SHARE		: apk_share( data ); break;
		case R.id.APK_MENU_EXTRACT 		: apk_extract( data ); break;
		}
					
		return true;
	}

	@SuppressLint("SdCardPath")
	private void apk_extract(ApkListData data) 
	{
		File apkFile = new File( data.getApkFilepath());
		File baseFile = new File( "/sdcard/AndroidManager" );
		if( !baseFile.exists()) baseFile.mkdirs();
		
		File destFile = new File( baseFile, data.getPackageName() + ".apk" );
		
		FileCopyAsync filecopy = new FileCopyAsync( context );
		filecopy.execute( apkFile, destFile );
	}



	/**
	 * @param data
	 */
	private void apk_share(ApkListData data) 
	{
		workStack.clear();
		workStack.push( data );
			
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("application/vnd.android.package-archive");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( new File( data.getApkFilepath() )));
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File..." );
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File..." );
		
		startActivity(Intent.createChooser(shareIntent, "Share APK File" ));
	}



	/**
	 * APK을 단말기에서 Uninstall 합니다. 
	 * 
	 * @param data
	 */
	private final int ID_ACTION_APK_UNINSTALL	= 0x1000;
	private void apk_uninstall(ApkListData data, int position ) 
	{
		workStack.clear();
		workStack.push( data );
		
		Uri packageURI = Uri.parse("package:"+data.getPackageName());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivityForResult( uninstallIntent, ID_ACTION_APK_UNINSTALL );
	}

	/**
	 * APK 을 다운받을 수 있는 마켓 페이지로 이동한다. 
	 * 
	 * @param data
	 */
	private void apk_goto_market(ApkListData data) 
	{
		workStack.clear();
		workStack.push( data );
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + data.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}

	/**
	 * APK 을 실행 한다. 
	 * 
	 * @param data
	 */
	private void apk_running(ApkListData data) 
	{
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(data.getPackageName());
	    if (intent != null) {
	    	workStack.clear();
	    	workStack.push( data );
	    	
	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		    
	    } else {
	    	apk_goto_market( data );
	    }
	}
	
	/**
	 * packageName 의 앱이 설치되어 있는지 확인해 준다. 
	 * 
	 * @param packageName
	 * @return
	 */
	private boolean is_apk_installed( String packageName ) {
		PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo( packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
	}
}
