package com.purehero.bluetooth.share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.purehero.common.FragmentEx;
import com.purehero.common.Utils;

public class ContactBackupFragment extends FragmentEx implements OnItemClickListener {
	private final MainActivity context;
	private View layout = null;

	ListView listView = null;
	ContactBackupAdapter adapter = null;
	List<File> listDatas = new ArrayList<File>();
	
	public ContactBackupFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout 				= inflater.inflate( R.layout.contact_backup, container, false);
		
		listView = ( ListView ) layout.findViewById( R.id.listView );
		if( listView != null ) {
			adapter = new ContactBackupAdapter( context );
			listView.setAdapter( adapter );
			listView.setOnItemClickListener( this );
			
			registerForContextMenu( listView );			
			adapter.getBackupContactDatas();
		}
		
		return layout;
	}
	
	@Override
	public boolean onBackPressed() {
		if( adapter.isShowCheckBox()) {
			adapter.setShowCheckBox( false );
			return true;
		}
		
		return super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		listView.showContextMenuForChild(view);
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.listView ) {
			context.getMenuInflater().inflate(R.menu.contact_backup, menu);
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			File file = ( File ) adapter.getItem( info.position );
			menu.setHeaderTitle( String.format( "'%s' Backup file", Utils.getPureFilename( file )));
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean ret = false;
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch( item.getItemId()) {
		case R.id.menu_backup_delete :
			adapter.remove( info.position );
			break;
		case R.id.menu_backup_restore :
			adapter.restore( info.position );
			break;
		}
		
		return ret;
	}
}
