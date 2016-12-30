package com.purehero.apk.manager;

import java.io.File;
import java.util.Stack;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class FileListFragment extends Fragment {
	private ListView fileListView 				= null;
	private FileListAdapter fileListAdapter 	= null;
	private Stack<FileListData> workStack 		= new Stack<FileListData>();
		
	private MainActivity context = null;
	private View layout = null;
	private TextView fileListPath = null;
    
	public FileListFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.file_list_layout, container, false); 
		
		fileListPath = ( TextView ) layout.findViewById( R.id.fileListPath );
		//fileListPath.setMovementMethod(new ScrollingMovementMethod());
		
		new Thread( file_info_load_runnable ).start();
		return layout;	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent ) {
		G.log( "FileListFragment::onActivityResult");
		
		workStack.clear();
					
		switch( requestCode ) {
		case R_ID_FILE_MENU_DELETE :	
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, intent);
	}

	Runnable file_info_load_runnable = new Runnable() {
		@Override
		public void run() 
		{
			// 초기화면을 표시하기 위해 필요한 데이터 수집
			getFileInfos();
			
			// 수집된 데이터 화면에 보여 주기
			context.runOnUiThread( init_ui_runnable );
		}
	};
	
	Runnable init_ui_runnable = new Runnable() {
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
		        	if( data.getFilename().compareTo("..") == 0 ) {
		        		fileListAdapter.back_folder();
		        		
		        	} else if( data.getFile().isDirectory()) {
		        		fileListAdapter.push_folder( data.getFile());
		        	} else {
		        		fileListView.showContextMenuForChild(view);
		        	}
		        	
		        	fileListPath.setText( fileListAdapter.getFolderPath() );
		        }
		    });
			
			fileListPath.setText( fileListAdapter.getFolderPath() );
		}
	};
	
	
	/**
	 * 설치된 앱(APK)들의 정보(아이콘, 앱이름, 패키지명)등을 추출한다. 
	 */
	private void getFileInfos() {
		File base = new File( "/sdcard");
		if( !base.exists()) {
			base = new File( "/");
		}
		
	    fileListAdapter = new FileListAdapter( context, base );
	    fileListAdapter.sort();
	}
	
		
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.fileListView ) {
			context.getMenuInflater().inflate(R.menu.file_context_menu, menu);
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		G.log( "onContextItemSelected" );
		
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		FileListData data = ( FileListData ) fileListAdapter.getItem( info.position );
		data.setIndex( info.position );
		G.log( "onContextItemSelected index : " + info.position );
		
		switch( item.getItemId()) {
		case R.id.FILE_MENU_RUNNING		: file_running( data ); 		break;
		case R.id.FILE_MENU_DELETE 		: file_delete( data, info.position ); 	break;
		case R.id.FILE_MENU_SHARE		: file_share( data ); break;
		}
							
		return false;
	}
	final int R_ID_FILE_MENU_RUNNING		= 1000;
	final int R_ID_FILE_MENU_DELETE 		= 1002;
	final int R_ID_FILE_MENU_SHARE 			= 1003;
	/**
	 * @param data
	 */
	private void file_share(FileListData data) {
		G.log( "file_share" );
		
		workStack.clear();
		workStack.push( data );
		
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.setType("multipart/");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( data.getFile() ));
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File..." );
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File..." );
		
		startActivityForResult(Intent.createChooser(shareIntent, "Share File" ), R_ID_FILE_MENU_SHARE);
	}



	/**
	 * FILE을 단말기에서 Delete 합니다. 
	 * 
	 * @param data
	 */	
	private void file_delete(FileListData data, int position ) {
		G.log( "file_delete" );
		
		workStack.clear();
		workStack.push( data );
		
		data.setIndex( position );
		new fileDeleteDialog( data ).show();
		//data.getFile().delete();
		//fileListAdapter.remove( position );
	}

	/**
	 * APK 을 실행 한다. 
	 * 
	 * @param data
	 */
	private void file_running(FileListData data) {
		G.log( "file_running" );
		
		workStack.clear();
		workStack.push( data );
		
		Intent myIntent = new Intent(Intent.ACTION_VIEW);
		myIntent.addCategory(Intent.CATEGORY_DEFAULT);
		//myIntent.setData(Uri.fromFile(data.getFile()));
		myIntent.setDataAndType( Uri.fromFile(data.getFile()), getMimeType( data.getFilename()));
		
		Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
		startActivityForResult(j, R_ID_FILE_MENU_RUNNING);
	}
	
	private String getMimeType(String url) {
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
	    if (extension != null) {
	        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
	
	class fileDeleteDialog {
		private FileListData data;
		public fileDeleteDialog( FileListData data ) {
			this.data = data;
		}
		
		public void show() {
			Builder dlg = new AlertDialog.Builder( context ); 
		    dlg.setTitle( R.string.delete ); 
		    dlg.setMessage( String.format( "'%s'\n\n%s", data.getFilename(), context.getString( R.string.file_delete_confirm ))); 
		        //.setIcon(R.drawable.delete)
		    dlg.setPositiveButton( R.string.delete, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) { 
		    		data.getFile().delete();
		    		fileListAdapter.remove( data.getIndex());
		    		dialog.dismiss();
		    	}   
		    });
		    dlg.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		dialog.dismiss();
		    	}
		    });
		    dlg.create().show();
		}
	}
}
