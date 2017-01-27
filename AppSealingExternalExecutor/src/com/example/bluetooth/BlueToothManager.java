package com.example.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BlueToothManager {
	public static final int REQUEST_ENABLE_BT = 1023;
	
	private BluetoothAdapter btAdapter 	= null;
	private Activity activity			= null;
	
	public BlueToothManager( Activity activity ) {
		this.activity = activity;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// 감지 권한을 요구하는 인텐트 작성
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		   
		// 부가 정보로 감지 가능한 시간을 지정할 수 있다 (300초가 최대임)
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

		// 활동을 시작 (사용자에게 권한을 요구하는 다이얼로그 표시)
		activity.startActivity(discoverableIntent);
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
	
	public void searchDevices() {
		// Bluetooth 인텐트 필터 작성
		//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	   	//브로드캐스트 리시버 등록
		//activity.registerReceiver(mReceiver, filter);
		// 주변 Bluetooth 디바이스 검색 시작 
		btAdapter.startDiscovery();
	}
	
	private final BroadcastReceiver mReceiver = new BlueToothBroadcastReceiver();
}
