package com.purehero.bluetooth.share;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.bluetooth.BluetoothManager;
import com.purehero.bluetooth.IFBluetoothEventListener;
import com.purehero.common.G;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BluetoothChatFragment extends Fragment implements OnClickListener {
	private final MainActivity context;
	private View layout = null;

	ListView listView = null;
	ArrayAdapter <String> adapter = null;
	List<String> listDatas = new ArrayList<String>();
	
	public BluetoothChatFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout 				= inflater.inflate( R.layout.bluetooth_chat, container, false);
		
		int btnIDs[] = { R.id.btnSend };
		for( int id : btnIDs ) {
			Button btn = ( Button ) layout.findViewById(id);
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		listView = ( ListView ) layout.findViewById( R.id.listView );
		if( listView != null ) {
			adapter = new ArrayAdapter <String> ( context, android.R.layout.simple_list_item_1, listDatas );
			listView.setAdapter( adapter );
		}
		
		return layout;
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnSend :
			EditText editText = (EditText) layout.findViewById( R.id.editText );
			if( editText != null ) {
				String msg = editText.getText().toString();
				G.Log( "message : %s", msg );
				
				
				byte [] msg_bytes = msg.getBytes(Charset.forName("UTF-8"));
					
				G.Log( "Send message : %s", msg );
				BluetoothManager.getInstance().write( msg_bytes, msg_bytes.length );
			}
			break;
		}
	}

	IFBluetoothEventListener bluetoothEventListenerreceiver = new IFBluetoothEventListener() {
		@Override
		public void OnDateReceived( byte[] data, int size ) {
			String msg = new String( data, 0, size );
			G.Log( "Received message : %s", msg );
		}

		@Override
		public void OnDisconnected() {
			G.Log( "Disconnected" );
		}

		@Override
		public void OnConnected( final BluetoothCommunication btComm) {
			G.Log( "OnConnected" );
			
		}
	};	
}
