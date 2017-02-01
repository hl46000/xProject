package com.purehero.bluetooth.share;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.bluetooth.BluetoothManager;
import com.purehero.bluetooth.IFBluetoothEventListener;
import com.purehero.common.G;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	ListView listView = null;
	ArrayAdapter <String> adapter = null;
	List<String> listDatas = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		G.init( this );
		BluetoothManager.getInstance().SetBluetoothEventListener( bluetoothEventListenerreceiver );
		
		int btnIDs[] = { R.id.btnSend };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById(id);
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		listView = ( ListView ) findViewById( R.id.listView );
		if( listView != null ) {
			adapter = new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, listDatas );
			listView.setAdapter( adapter );
		}
	}
	
	DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch( which ) {
			case G.DIALOG_BUTTON_ID_YES : 
				G.Log( "input dialog result : %s", G.getTextInputDialogResult());
				break;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		BluetoothManager.getInstance().release();
		super.onDestroy();
	}

	Runnable listViewReflash = new Runnable(){
		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};
	
	IFBluetoothEventListener bluetoothEventListenerreceiver = new IFBluetoothEventListener() {
		@Override
		public void OnDateReceived( byte[] data, int size ) {
			String msg = new String( data, 0, size );
			listDatas.add(msg);
			
			MainActivity.this.runOnUiThread( listViewReflash );
			
			G.Log( "Received message : %s", msg );
		}

		@Override
		public void OnDisconnected() {
			G.Log( "Disconnected" );
		}

		@Override
		public void OnConnected( final BluetoothCommunication btComm) {
			G.Log( "OnConnected" );
			
			MainActivity.this.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					TextView tv = ( TextView ) findViewById( R.id.tvStatue );
					tv.setText( btComm.getName() );
				}});
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.btConnect) {
			BluetoothManager.getInstance().openDeviceList( this );
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnSend :
			EditText editText = (EditText) findViewById( R.id.editText );
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
}
