package com.purehero.bluetooth.share;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import android.widget.Toast;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.bluetooth.IFBluetoothConnectionReceiver;
import com.purehero.bluetooth.BluetoothManager;
import com.purehero.bluetooth.DeviceListActivity;
import com.purehero.bluetooth.IFBluetoothDataReceiver;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	BluetoothCommunication btComm = null;
	
	ListView listView = null;
	ArrayAdapter <String> adapter = null;
	List<String> listDatas = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BluetoothManager.getInstance().initialize( this );
		
		int btnIDs[] = { R.id.btnDeviceList, R.id.btnAccept, R.id.btnSend };
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
		
		G.Log( "MainActivity" );
	}
	
	@Override
	protected void onDestroy() {
		if( btComm != null ) {
			btComm.release();
		}
		BluetoothManager.getInstance().release();
		super.onDestroy();
	}

	Runnable listViewReflash = new Runnable(){
		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};
	IFBluetoothDataReceiver bluetoothDataReceiver = new IFBluetoothDataReceiver() {
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
	};
	
	IFBluetoothConnectionReceiver newBluetoothConnection = new IFBluetoothConnectionReceiver() {
		@Override
		public void receivedConnection(BluetoothCommunication comm) {
			G.Log( "receivedConnection" );
			if( btComm != null && btComm.isConnected()) { 
				comm.release();
				return;
			} 
			btComm = comm;
			btComm.setDataReceiver( bluetoothDataReceiver );
			
			MainActivity.this.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					TextView tv = ( TextView ) findViewById( R.id.tvHello );
					tv.setText( btComm.getName() );
				}});			
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { 
		case BluetoothManager.REQUEST_ENABLE_BT: // When the request to enable Bluetooth returns 
			if (resultCode == Activity.RESULT_OK) { // 확인 눌렀을 때 //Next Step 
				try {
					BluetoothManager.getInstance().startReceiveClient( "btServer", newBluetoothConnection );
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else { // 취소 눌렀을 때 
				Toast.makeText( this, "블루투스가 활성화되지 않아서 앱을 종료 합니다.", Toast.LENGTH_LONG ).show();
				this.finish();
			} 
			break;
			
		case BluetoothManager.REQUEST_CONNECT_DEVICE :
			if( resultCode == Activity.RESULT_OK ) {
				String address = data.getStringExtra( DeviceListActivity.EXTRA_DEVICE_ADDRESS );
				
				BluetoothDevice device = BluetoothManager.getInstance().getDevice( address );
				if( device != null ) {
					try {
						BluetoothCommunication newComm = BluetoothManager.getInstance().connectDevice( device, true );
						newBluetoothConnection.receivedConnection( newComm );
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
			break;
			
			// 블루투스 연결 허용 
		case BluetoothManager.REQUEST_DISCOVERABLE_DEVICE :
			if( resultCode != 0 ) {
				int duration = resultCode;
				new discoverableDeviceThread( this, (Button) findViewById(R.id.btnAccept ), duration ).start();
				
				try {
					BluetoothManager.getInstance().startReceiveClient( "btServer", newBluetoothConnection );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnDeviceList :
			if( BluetoothManager.getInstance().enableDevice()) {
				BluetoothManager.getInstance().searchDevices();
			} else {
				BluetoothManager.getInstance().requestEnable();
			}
			break;
			
		case R.id.btnAccept :
			if( BluetoothManager.getInstance().enableDevice()) {
				BluetoothManager.getInstance().discoverableDevice( 300 );				
			} else {
				BluetoothManager.getInstance().requestEnable();
			}
			break;
			
		case R.id.btnSend :
			EditText editText = (EditText) findViewById( R.id.editText );
			if( editText != null ) {
				String msg = editText.getText().toString();
				G.Log( "message : %s", msg );
				
				if( btComm != null ) {
					byte [] msg_bytes = msg.getBytes(Charset.forName("UTF-8"));
					
					G.Log( "Send message : %s", msg );
					btComm.write( msg_bytes, msg_bytes.length );
				}
			}
			break;
		}
	}
	
	class discoverableDeviceThread extends Thread implements Runnable {
		
		private final Activity act;
		private final Button btn;
		private final String orgButtonTitle;
		private int duration = 300;
		
		public discoverableDeviceThread( Activity act, Button btn, int duration ) {
			this.act = act;
			this.btn = btn;
			this.orgButtonTitle = (String) btn.getText();
			this.duration = duration;
		}
		
		@Override
		public void run() {
			while( duration > 1 ) {
				try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				duration--;
				act.runOnUiThread( action );
			}
			btn.setText( String.format( "%s", orgButtonTitle ));
		}
		
		Runnable action = new Runnable() {
			@Override
			public void run() {
				btn.setText( String.format( "%s(%d초)", orgButtonTitle, duration ));
			}
		};
	};
}
