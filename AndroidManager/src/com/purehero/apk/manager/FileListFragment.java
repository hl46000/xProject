package com.purehero.apk.manager;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.InterstitialAd;

public class FileListFragment extends Fragment {
	private ListView fileListView 				= null;
	private FileListAdapter fileListAdapter 	= null;
	private InterstitialAd interstitialAd		= null;	// 전면 광고
	
	private Activity context = null;
	private View layout = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.file_list_layout, container, false); 
		
		new Thread( file_info_load_runnable ).start();
		return layout;	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent ) 
	{
		FileListData data = null;
		
		switch( requestCode ) {
		case R_ID_FILE_MENU_DELETE :	
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, intent);
	}

	Runnable file_info_load_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// 초기화면을 표시하기 위해 필요한 데이터 수집
			getFileInfos();
			
			// 수집된 데이터 화면에 보여 주기
			context.runOnUiThread( init_ui_runnable );
		}
	};
	
	Runnable init_ui_runnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			// ListView 나타나게 하기
			fileListView	= ( ListView ) layout.findViewById( R.id.fileListView );
			fileListView.setVisibility( View.VISIBLE );
			fileListView.setAdapter( fileListAdapter );			
			registerForContextMenu( fileListView );
			
			fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        	FileListData data = ( FileListData ) fileListAdapter.getItem( position );
		        	fileListAdapter.reload(context, data.getFile());
		        }
		    });
		}
	};
	
	
	/**
	 * 설치된 앱(APK)들의 정보(아이콘, 앱이름, 패키지명)등을 추출한다. 
	 */
	private void getFileInfos() 
	{
	    fileListAdapter = new FileListAdapter( context, new File( "/sdcard") );
	    fileListAdapter.sort();
	}
			
	
		
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		if ( v.getId() == R.id.fileListView ) {
			context.getMenuInflater().inflate(R.menu.contextual, menu);
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		FileListData data = ( FileListData ) fileListAdapter.getItem( info.position );
		data.setIndex( info.position );
		
		/*
		switch( item.getItemId()) {
		case R.id.APK_MENU_RUNNING		: apk_running( data ); 		break;
		case R.id.APK_MENU_GOTO_MARKET	: apk_goto_market( data ); 	break;
		case R.id.APK_MENU_DELETE 		: apk_uninstall( data, info.position ); 	break;
		case R.id.APK_MENU_SHARE		: apk_share( data ); break;
		case R.id.APK_MENU_EXTRACT 		: apk_extract( data ); break;
		case R.id.APK_MENU_INFOMATION	: apk_infomation( data ); break;
		}
		*/
					
		return true;
	}
	final int R_ID_FILE_MENU_RUNNING		= 1000;
	final int R_ID_FILE_MENU_DELETE 		= 1002;
	final int R_ID_FILE_MENU_SHARE 			= 1003;
	/**
	 * @param data
	 */
	private void file_share(FileListData data) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("application/vnd.android.package-archive");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( data.getFile() ));
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File..." );
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File..." );
		
		startActivityForResult(Intent.createChooser(shareIntent, "Share APK File" ), R_ID_FILE_MENU_SHARE);
	}



	/**
	 * FILE을 단말기에서 Delete 합니다. 
	 * 
	 * @param data
	 */	
	private void apk_delete(FileListData data, int position ) 
	{
		/*
		Uri packageURI = Uri.parse("package:"+data.getPackageName());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivityForResult( uninstallIntent, R_ID_FILE_MENU_DELETE );
		*/		
	}

	/**
	 * APK 을 실행 한다. 
	 * 
	 * @param data
	 */
	private void file_running(FileListData data) 
	{
		/*
		Intent intent = context.getPackageManager().get(data.getFile()));
	    if (intent != null) {
	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivityForResult(intent, R_ID_FILE_MENU_RUNNING);
	    }
	    */
	}
}
