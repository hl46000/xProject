package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BlueToothBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent intent) {
		String action = intent.getAction();
	     
		// 디바이스가 발견됨 (이름이 밝혀졌다)
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	       // 인텐트에 포함된 Bluetooth 디바이스 오브젝트를 얻는다
	       BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	       // 디바이스명과 MAC 주소 출력
	       Log.d( "BluetoothChat", device.getName() + ", " + device.getAddress());  
	     }
	}
}
