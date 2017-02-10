package com.purehero.bluetooth.share;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;
import com.purehero.contact.ContactAdapter;
import com.purehero.contact.ContactData;
import com.purehero.contact.ContactUtils;

public class ContactFragment extends FragmentEx implements OnItemClickListener, OnItemLongClickListener {
	private final MainActivity context;
	private View layout = null;

	private ListView listView = null;
	private ProgressBar progressBar = null;
	private ContactAdapter adapter;
	
	public ContactFragment(MainActivity mainActivity) {
		context = mainActivity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		G.Log( "onCreateView" );
		
		layout 		= inflater.inflate( R.layout.contact_list, container, false);
		listView	= ( ListView ) layout.findViewById( R.id.listView );
		listView.setOnItemClickListener( this );
		listView.setOnItemLongClickListener( this );
		registerForContextMenu( listView );
		
		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		
		new Thread( getContactRunnable ).start();
		
		return layout;
	}
	
	Runnable getContactRunnable = new Runnable() {
		@Override
		public void run() {
			G.Log( "run" );			
			if( adapter == null ) {
				adapter = new ContactAdapter( context );
				adapter.getContactDatas();
				
				context.setContactAdapter( adapter );
			}
			
			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					G.Log( "runOnUiThread run" );
					listView.setAdapter( adapter );
					
					adapter.notifyDataSetChanged();
					progressBar.setVisibility( View.INVISIBLE );
					
					// 검색
					EditText search = (EditText) layout.findViewById( R.id.txt_search );
					if( search != null ) {
						search.addTextChangedListener(new TextWatcher() {
					        @Override
					        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					        	adapter.getFilter().filter(cs);
					        }
					        @Override
					        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
					        @Override
					        public void afterTextChanged(Editable arg0) { }
					    });
					}
				}});
		}
	};

	
	
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
		ContactData data = ( ContactData ) adapter.getItem( position );
		
		if( adapter.isShowCheckBox() ) {
			data.setSelected( !data.isSelected() );
			adapter.notifyDataSetChanged();
		} else {		
			ContactUtils.openDetailView( context, data.getContactID() );
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if( adapter.isShowCheckBox()) {
			return false;			
		}
		adapter.setShowCheckBox( !adapter.isShowCheckBox() );
		return true;
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.listView ) {
			context.getMenuInflater().inflate(R.menu.contact, menu);

			int selected_count = adapter.getCheckedCount();
			menu.findItem( R.id.menu_send_to_my ).setVisible( false );
			menu.findItem( R.id.menu_send_to_remote ).setVisible( context.getRemoteContactAdapter().isConnected() && selected_count > 0 );
			if( selected_count == 0 ) {				
				menu.findItem( R.id.menu_delete ).setVisible( false );
				menu.findItem( R.id.menu_clear_all ).setVisible( false );
				menu.findItem( R.id.menu_backup_selected_contacts ).setVisible( false );
			}
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		G.Log( "onContextItemSelected" );
		
		boolean ret = false;	// 메뉴의 처리 여부 
		
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		G.Log( "onContextItemSelected index : " + info.position );
		
		//ContactData data = ( ContactData ) adapter.getItem( info.position );
		
		switch( item.getItemId()) {
		case R.id.menu_send_to_remote : 
			ret = true;
			break;
		case R.id.menu_send_to_my : 
			ret = true;
			break;
		case R.id.menu_delete :
			G.confirmDialog( context, "삭제 확인", String.format( "선택된 %d개의 연락처를 삭제하시겠습니까?", adapter.getCheckedCount()), 0, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch( arg1 ) {
					case G.DIALOG_BUTTON_ID_YES :
						adapter.deleteCheckedItems();
						adapter.notifyDataSetChanged();
						break;
					}
				}});			
			ret = true;
			break;
		case R.id.menu_select_all : 
			adapter.setAllChecked( true );
			adapter.notifyDataSetChanged();
			ret = true;
			break;
		case R.id.menu_clear_all : 
			adapter.setAllChecked( false );
			adapter.notifyDataSetChanged();	
			ret = true;
			break;
		case R.id.menu_backup_selected_contacts :
			G.textInputDialog( context, "BACKUP", 
					String.format( "%d 개의 연락처를 백업합니다.", adapter.getCheckedCount()), 
					"백업명을 입력하여 주세요", 0, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch( arg1 ) {
					case G.DIALOG_BUTTON_ID_YES :
						adapter.backupCheckedItems( G.getTextInputDialogResult() );
						break;
					}
				}});
			
			ret = true;	
			break;
		}
							
		return ret;
	}
}
