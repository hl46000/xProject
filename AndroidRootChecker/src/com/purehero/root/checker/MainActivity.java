package com.purehero.root.checker;

import java.io.File;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private final String LOG_TAG = "RootChecker";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d( LOG_TAG, "This Device is " + ( isRooted() ? "Rooted!!" : "not Rooted"));
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
	    return findSuBinary() || fineSuperSuApk();
	}

	private boolean fineSuperSuApk() {
		String places [] = {
	    		"/system/app/Superuser.apk", 
	    		"/data/app/eu.chainfire.supersu.apk",
	    		"/data/app/eu.chainfire.supersu-1.apk" 
	    };
	    
	    for (String where : places) {
	    	File file = new File( where );
	    	if ( file.exists() ) {
	    		Log.d( LOG_TAG, "Found su apk file at : " + file.getAbsolutePath());
	    		return true;
	    	}
	    }
	    return false;
	}
	
	private boolean findSuBinary() {
	    String places [] = {
	    		"/sbin/su", 
	    		"/system/bin/su", 
	    		"/system/xbin/su", 
	    		"/data/local/xbin/su",
	            "/data/local/bin/su", 
	            "/system/sd/xbin/su", 
	            "/system/bin/failsafe/su", 
	            "/data/local/su",
	            "/system/xbin/daemonsu",
	            "/su/bin/su",
	            "/su/xbin/su",
	            "/su/bin/daemonsu",
	    };
	    
	    for (String where : places) {
	    	File file = new File( where );
	    	if ( file.exists() ) {
	    		Log.d( LOG_TAG, "Found su binary at : " + file.getAbsolutePath());
	    		return true;
	    	}
	    }
	    return false;
	}
	
	private boolean testSuBinary() {
		return false;
	}
}
