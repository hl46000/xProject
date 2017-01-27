package com.purehero.contact.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	private BlueToothManager bluetoothManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bluetoothManager = new BlueToothManager( this );
		
		int btnIDs[] = { R.id.btnDeviceList };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById(id);
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		if( bluetoothManager != null ) {
			bluetoothManager.release();
		}
		super.onDestroy();
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
			if( bluetoothManager.enableDevice()) {
				bluetoothManager.searchDevices2();
			} else {
				bluetoothManager.requestEnable();
			}
			
			break;
		}
	}
}
