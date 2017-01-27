package com.purehero.contact.share;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

public class BlueToothManager {
	public static final int REQUEST_ENABLE_BT 		= 1023;
	public static final int REQUEST_CONNECT_DEVICE 	= 1024;
	
	private BluetoothAdapter btAdapter 	= null;
	private BluetoothBroadcastReceiver bluetoothBR = null;
	private final Activity activity;
	
	public BlueToothManager( Activity activity ) {
		this.activity = activity;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothBR = new BluetoothBroadcastReceiver( activity );
		
		Set<BluetoothDevice> boundedDevices = btAdapter.getBondedDevices();
		for( BluetoothDevice device : boundedDevices ) {
			G.Log( "Bounded device : %s[%s]", device.getName(), device.getAddress());
		}
	}
	
	/**
	 * 다른 장치에서 내 장치를 찾을 수 있도록 duration 초동안 노출 시켜 준다. 
	 * 
	 * @param duration
	 */
	public void discoverableDevice( int duration ) {
		// 감지 권한을 요구하는 인텐트 작성
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		   
		// 부가 정보로 감지 가능한 시간을 지정할 수 있다 (300초가 최대임)
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);

		// 활동을 시작 (사용자에게 권한을 요구하는 다이얼로그 표시)
		activity.startActivity(discoverableIntent);
	}
	
	/**
	 * Bluetooth 장치가 사용 가능한지를 반환한다. 
	 * 
	 * @return true: 사용가능, false: 사용불가
	 */
	public boolean enableDevice() { 
		return btAdapter != null;  
	}

	/**
	 * Bluetooth 장치를 사용가능하도록 설정창을 띄워 준다. 
	 */
	public void requestEnable() { 
		if( !btAdapter.isEnabled()) {	
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
			activity.startActivityForResult(i, REQUEST_ENABLE_BT); 
		}
	}
	
	public void searchDevices2() { 
		G.Log( "Scan Device" ); 
		Intent serverIntent = new Intent( activity, DeviceListActivity.class); 
		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); 
	}

	
	
	/**
	 * 다른 사용가능한 장치를 검색한다. 
	 */
	public void searchDevices() {
		if( btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		
		bluetoothBR.register();
		
		btAdapter.startDiscovery();
		G.Log( "searchDevices" );
	}

	public void release() {
		bluetoothBR.unregister();
	}
}
