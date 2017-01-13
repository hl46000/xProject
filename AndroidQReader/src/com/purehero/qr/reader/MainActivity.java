package com.purehero.qr.reader;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.ViewfinderView;

public class MainActivity extends com.google.zxing.client.android.CaptureActivity {
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    resultView = findViewById(R.id.result_view);
	    statusView = (TextView) findViewById(R.id.status_view);
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

	private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
	private long backPressedTime = 0;
		
	@Override
	public void onBackPressed() 
	{
		if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
			super.onBackPressed();
			
		} else {
			backPressedTime = System.currentTimeMillis();
			Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
		}
	}
}
