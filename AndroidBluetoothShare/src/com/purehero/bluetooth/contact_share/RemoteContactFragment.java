package com.purehero.bluetooth.contact_share;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.bluetooth.BluetoothManager;
import com.purehero.bluetooth.IFBluetoothEventListener;
import com.purehero.bluetooth.contact_share.R;
import com.purehero.common.FragmentEx;
import com.purehero.common.G;

public class RemoteContactFragment extends FragmentEx implements OnClickListener {
	private final MainActivity context;
	private View layout = null;

	private ListView listView = null;
	private ProgressBar progressBar = null;
	private final RemoteContactAdapter adapter;
	
	public RemoteContactFragment(MainActivity mainActivity) {
		context = mainActivity;
		adapter = new RemoteContactAdapter( context );
		context.setRemoteContactAdapter( adapter );
		
		BluetoothManager.getInstance().SetBluetoothEventListener( bluetoothEventListenerreceiver );
	}
	
	public IFBluetoothEventListener bluetoothEventListenerreceiver = new IFBluetoothEventListener() {
		@Override
		public void OnDataReceived( byte[] data, int size ) {
			adapter.dataReceived( data, size );
		}

		@Override
		public void OnDisconnected() {
			adapter.disconnected();
			
			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					TextView tv = ( TextView ) layout.findViewById( R.id.tvStatue );
					tv.setText( R.string.no_connected );
					
					Button btn = ( Button ) layout.findViewById( R.id.btnRemoteDevice );
					if( btn != null ) {
						btn.setText( R.string.connect);
					}
				}}
			);
			
			G.Log( "Disconnected" );
		}

		@Override
		public void OnConnected( final BluetoothCommunication _btComm) {
			adapter.connected( _btComm, context.getContactAdapter() );
			
			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					TextView tv = ( TextView ) layout.findViewById( R.id.tvStatue );
					tv.setText( _btComm.getName() );
					
					Button btn = ( Button ) layout.findViewById( R.id.btnRemoteDevice );
					if( btn != null ) {
						btn.setText( R.string.get_contact_list);
					}
				}}
			);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		G.Log( "onCreateView" );
		
		layout 		= inflater.inflate( R.layout.remote_contact_list, container, false);
		listView	= ( ListView ) layout.findViewById( R.id.listView );
		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		listView.setAdapter( adapter );
		
		int btnIDs [] = { R.id.btnRemoteDevice };
		for( int id : btnIDs ) {
			Button btn = ( Button ) layout.findViewById(id);
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		new Thread( init_view_thread ).start();
		
		return layout;
	}
	
	Runnable init_view_thread = new Runnable() {
		@Override
		public void run() {
			G.Log( "run" );			

			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					G.Log( "runOnUiThread run" );

					progressBar.setVisibility( View.INVISIBLE );
					listView.setVisibility( View.VISIBLE );
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == 100 ) {
			adapter.deleteCacheFiles();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onBackPressed() {
		if( adapter.isShowCheckBox()) {
			adapter.setAllChecked( false );
			adapter.setShowCheckBox( false );
			return true;
		}
		
		return super.onBackPressed();
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.listView ) {
			context.getMenuInflater().inflate(R.menu.contact_remote, menu);
			
			if( adapter.getCheckedCount() == 0 ) {
				menu.findItem( R.id.menu_send_to_my ).setVisible( false );
				menu.findItem( R.id.menu_remote_delete ).setVisible( false );
				menu.findItem( R.id.menu_remote_clear_all ).setVisible( false );				
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
		case R.id.menu_send_to_my : 
			adapter.sendRequestContactDatas();
			ret = true;
			break;
		case R.id.menu_remote_delete : 
			adapter.sendDeleteContacts();
			ret = true;
			break;
		case R.id.menu_remote_select_all : 
			adapter.setAllChecked( true );
			adapter.notifyDataSetChanged();
			ret = true;
			break;
		case R.id.menu_remote_clear_all : 
			adapter.setAllChecked( false );
			adapter.notifyDataSetChanged();
			ret = true;
			break;
		}
							
		return ret;
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnRemoteDevice :
			Button btn = ( Button ) arg0;
			if( getString( R.string.get_contact_list ).compareTo( btn.getText().toString() ) == 0 ) {
				adapter.sendRequestContactList();
				
			} else {
				BluetoothManager.getInstance().openDeviceList( this );
			}
						
			break;
		}
	}

}
