package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.appsealingexternalexecutor.R;

public class BlueToothActivity extends Activity {

	BlueToothManager bluetoothManager = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.activity_main);
		
		bluetoothManager = new BlueToothManager( this );
		if( bluetoothManager.enableDevice()) {
			bluetoothManager.requestEnableBluetooth();
		} else {
			bluetoothManager.searchDevices();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { 
		case BlueToothManager.REQUEST_ENABLE_BT: // When the request to enable Bluetooth returns 
			if (resultCode == Activity.RESULT_OK) { // 확인 눌렀을 때 //Next Step 
				bluetoothManager.searchDevices();
				
			} else { // 취소 눌렀을 때 
				Toast.makeText( this, "블루투스가 활성화되지 않아서 앱을 종료 합니다.", Toast.LENGTH_LONG ).show();
				this.finish();
			} 
			break;
		}
	}
}