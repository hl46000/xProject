package com.example.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BlueToothManager {
	public static final int REQUEST_ENABLE_BT = 1023;
	
	private BluetoothAdapter btAdapter 	= null;
	private Activity activity			= null;
	
	public BlueToothManager( Activity activity ) {
		this.activity = activity;
		btAdapter = BluetoothAdapter.getDefaultAdapter();		
	}
	
	public boolean enableDevice() { 
		return btAdapter != null;  
	}

	public void requestEnableBluetooth() { 
		if( !btAdapter.isEnabled()) {	
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
			activity.startActivityForResult(i, REQUEST_ENABLE_BT); 
		}
	}
}
