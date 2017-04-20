package com.purehero.bluetooth.share.apps;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.MainActivity;
import com.purehero.bluetooth.share.R;
import com.purehero.module.fragment.FragmentEx;

import java.io.File;
import java.util.Stack;

public class ApkListFragment extends FragmentEx {
	final int VIEW_MODE_LIST = 0;
	final int VIEW_MODE_GRID = 1;

	private ListView apkListView 			= null;
	private GridView apkGridView				= null;
	private ProgressBar progressBar  		= null;
	private ApkListAdapter apkListAdapter 	= null;
	private Stack<ApkListData> workStack 	= new Stack<ApkListData>();

	int view_layout_mode = VIEW_MODE_LIST;
	private MainActivity context = null;
	private View layout = null;
	
	public ApkListFragment setMainActivity( MainActivity activity ) {
		context = activity;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.apps_layout, container, false);

        // Fragment 가 option menu을 가지고 있음을 알림
        setHasOptionsMenu(true);

        // ActionBar Title 변경
        AppCompatActivity ACActivity = ( AppCompatActivity ) getActivity();
        ActionBar aBar = ACActivity.getSupportActionBar();
        if( aBar != null ) {
            aBar.setTitle( R.string.apps );
        }

		new Thread( apk_info_load_runnable ).start();
		return layout;	
	}

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent ) {
		G.Log( "ApkListFragment::onActivityResult");

		ApkListData data = null;

		if( !workStack.isEmpty()) {
			data = workStack.pop();
		}

		switch( requestCode ) {
		case R_ID_APK_MENU_DELETE :	

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

	Runnable apk_info_load_runnable = new Runnable() {
		@Override
		public void run() {
			// 초기화면을 표시하기 위해 필요한 데이터 수집
			getApkInfos();
			
			// 수집된 데이터 화면에 보여 주기
			context.runOnUiThread( init_ui_runnable );
		}
	};
	
	Runnable init_ui_runnable = new Runnable() {
		@Override
		public void run() {
			// Progress bar 사라지게 하기
			progressBar = ( ProgressBar ) layout.findViewById( R.id.progressBar );
			progressBar.setVisibility( View.GONE );
			progressBar = null;

			apkListView = (ListView) layout.findViewById(R.id.apkListView);
			apkGridView = (GridView) layout.findViewById(R.id.apkGridView);

			if( view_layout_mode == VIEW_MODE_LIST ) {
				// ListView 나타나게 하기
				apkGridView.setVisibility(View.GONE);
				apkListView.setVisibility(View.VISIBLE);
				apkListView.setAdapter(apkListAdapter);
				registerForContextMenu(apkListView);

				apkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						ApkListData data = (ApkListData) apkListAdapter.getItem(position);
						apk_running(data);

						data.setClickCount(data.getClickCount() + 1);
					}
				});

			} else {
				apkListView.setVisibility(View.GONE);
				apkGridView.setVisibility(View.VISIBLE);
				apkGridView.setAdapter(apkListAdapter);
				registerForContextMenu(apkGridView);

				apkGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						ApkListData data = (ApkListData) apkListAdapter.getItem(position);
						apk_running(data);

						data.setClickCount(data.getClickCount() + 1);
					}
				});
			}

			EditText apk_search = (EditText) layout.findViewById( R.id.txt_search );
			if( apk_search != null ) {
				apk_search.addTextChangedListener(new TextWatcher() {
			        @Override
			        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			        	ApkListFragment.this.apkListAdapter.getFilter().filter(cs);
			        }
			        @Override
			        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
			        @Override
			        public void afterTextChanged(Editable arg0) { }
			    });
			}
		}
	};
	
	
	/**
	 * 설치된 앱(APK)들의 정보(아이콘, 앱이름, 패키지명)등을 추출한다. 
	 */
	private void getApkInfos() {
	    apkListAdapter = new ApkListAdapter( context );
	    apkListAdapter.sort();
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id ) {
			case R.id.action_view_mode :
				if( view_layout_mode == VIEW_MODE_LIST ) {
					item.setIcon( R.drawable.ic_view_headline_white_24dp);
					view_layout_mode = VIEW_MODE_GRID;
				} else {
					item.setIcon( R.drawable.ic_view_module_white_24dp);
					view_layout_mode = VIEW_MODE_LIST;
				}
				new Thread( apk_info_load_runnable ).start();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.apps_option_menu, menu);
    }

	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.apkListView ) {
			context.getMenuInflater().inflate(R.menu.apps_context_menu, menu);
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean ret = false;
		
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ApkListData data = ( ApkListData ) apkListAdapter.getItem( info.position );
				
		data.setIndex( info.position );
		data.setClickCount( data.getClickCount() + 1 );
		
		switch( item.getItemId()) {
		case R.id.APK_MENU_RUNNING		: apk_running( data ); 		ret=true; break;
		case R.id.APK_MENU_GOTO_MARKET	: apk_goto_market( data ); 	ret=true; break;
		case R.id.APK_MENU_DELETE 		: apk_uninstall( data, info.position ); 	ret=true; break;
		case R.id.APK_MENU_SHARE		: apk_share( data ); ret=true; break;
		case R.id.APK_MENU_EXTRACT 		: apk_extract( data ); ret=true; break;
		case R.id.APK_MENU_INFOMATION	: apk_infomation( data ); ret=true; break;
		}
					
		return ret;
	}

	final int R_ID_APK_MENU_RUNNING		= 1000;
	final int R_ID_APK_MENU_GOTO_MARKET = 1001;
	final int R_ID_APK_MENU_DELETE 		= 1002;
	final int R_ID_APK_MENU_SHARE 		= 1003;
	final int R_ID_APK_MENU_EXTRACT 	= 1004;
	final int R_ID_APK_MENU_INFOMATION 	= 1005;
	
	/**
	 * @param data
	 */
	private void apk_infomation(ApkListData data) {
		try {
		    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		    intent.setData(Uri.parse("package:" + data.getPackageName()));
		    startActivityForResult(intent, R_ID_APK_MENU_INFOMATION);

		} catch ( ActivityNotFoundException e ) {
		    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
		    startActivityForResult(intent, R_ID_APK_MENU_INFOMATION );
		}
	}

	@SuppressLint("SdCardPath")
	private void apk_extract(ApkListData data) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		File apkFile = new File( data.getApkFilepath());
		File externalStorageFolder = Environment.getExternalStorageDirectory();
		File baseFile = new File(  sharedPref.getString( "sdcard_path", new File( externalStorageFolder, "ApkExtractor" ).getAbsolutePath()));
		if( !baseFile.exists()) baseFile.mkdirs();

		File destFile = new File( baseFile, data.getPackageName() + ".apk" );
		
		FileCopyAsync filecopy = new FileCopyAsync( context, data.getAppName() );
		filecopy.execute( apkFile, destFile );
	}



	/**
	 * @param data
	 */
	private void apk_share(ApkListData data) {
		workStack.clear();
		workStack.push( data );
			
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		try {
			String extension = getFileExt( data.getApkFilepath() );
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( extension.toLowerCase() );
			shareIntent.setDataAndType( Uri.fromFile( new File( data.getApkFilepath() )), mimeType );
		} catch (Exception e) {
			e.printStackTrace();
		}
		//shareIntent.setType("application/vnd.android.package-archive");
		//shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( new File( data.getApkFilepath() )));
		//shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File..." );
		//shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File..." );
		
		startActivityForResult(Intent.createChooser(shareIntent, "Share APK File" ), R_ID_APK_MENU_SHARE);
	}

	private String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}


	/**
	 * APK을 단말기에서 Uninstall 합니다. 
	 * 
	 * @param data
	 */	
	private void apk_uninstall(ApkListData data, int position ) {
		workStack.clear();
		workStack.push( data );
		
		Uri packageURI = Uri.parse("package:"+data.getPackageName());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivityForResult( uninstallIntent, R_ID_APK_MENU_DELETE );
		
	}

	/**
	 * APK 을 다운받을 수 있는 마켓 페이지로 이동한다. 
	 * 
	 * @param data
	 */
	private void apk_goto_market(ApkListData data) {
		workStack.clear();
		workStack.push( data );
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + data.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, R_ID_APK_MENU_GOTO_MARKET);
        
	}

	/**
	 * APK 을 실행 한다. 
	 * 
	 * @param data
	 */
	private void apk_running(ApkListData data) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(data.getPackageName());
	    if (intent != null) {
	    	workStack.clear();
	    	workStack.push( data );
	    	
	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivityForResult(intent, R_ID_APK_MENU_RUNNING);
	    	
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
