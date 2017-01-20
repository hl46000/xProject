package com.purehero.flash.light;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private FlashLight2 flashLight = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int btnIDs [] = new int[]{ R.id.btnFlash };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		flashLight = new FlashLight2( this );
		if( !flashLight.init()) {
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton( AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
		}
	}

	@Override
	protected void onDestroy() {
		flashLight.release();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	private boolean flash = false;
	
	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnFlash :
			flash = !flash;
			
			Button btn = ( Button ) arg0;
			btn.setText( flash ? "Flash OFF" : "Flash ON" );
			
			flashLight.setFlashLight( flash );
			break;
		}
		
	}
}
