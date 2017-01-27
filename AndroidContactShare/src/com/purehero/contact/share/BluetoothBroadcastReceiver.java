package com.purehero.contact.share;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	private final Activity activity;
	public BluetoothBroadcastReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		String action = intent.getAction();
		G.Log( "onReceived : %s", action );
		
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if( device != null ) {
			G.Log( device.getName() + ", " + device.getAddress());
		}
		
		switch( action ) {
		case BluetoothAdapter.ACTION_STATE_CHANGED :
			break;
		case BluetoothDevice.ACTION_ACL_CONNECTED :
			break;
		case BluetoothDevice.ACTION_ACL_DISCONNECTED :
			break;
		case BluetoothDevice.ACTION_FOUND :
			break;
		case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :	// 
			break;
		}
	}
	
	private IntentFilter filter = null; 
	public void register() {
		// Bluetooth 인텐트 필터 작성
		if( filter == null ) {
			filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction( BluetoothDevice.ACTION_ACL_CONNECTED );
			filter.addAction( BluetoothDevice.ACTION_ACL_DISCONNECTED );
			filter.addAction( BluetoothDevice.ACTION_NAME_CHANGED );
			//브로드캐스트 리시버 등록
			activity.registerReceiver( this, filter);
			
			filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			filter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );
			activity.registerReceiver( this, filter);
			
			G.Log( "registerReceiver" );
		}
	}
	
	public void unregister() {
		try {
			activity.unregisterReceiver( this );
			filter = null;
			G.Log( "unregisterReceiver" );
        } catch (IllegalArgumentException e){
        } catch (Exception e) {
        }finally {
        }
	}
}
