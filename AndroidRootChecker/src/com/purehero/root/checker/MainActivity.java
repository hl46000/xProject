package com.purehero.root.checker;

import java.io.File;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean isRooted() {
	    return findBinary("su");
	}

	public boolean findBinary(String binaryName) {
	    String places [] = {
	    		"/sbin/", 
	    		"/system/bin/", 
	    		"/system/xbin/", 
	    		"/data/local/xbin/",
	            "/data/local/bin/", 
	            "/system/sd/xbin/", 
	            "/system/bin/failsafe/", 
	            "/data/local/"
	    };
	    
	    for (String where : places) {
	    	if ( new File( where + binaryName ).exists() ) {
	    		return true;
	    	}
	    }
	    return false;
	}
}
