package com.example.myandroidsampleapp00;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	final String LOG_TAG = "TEST_Activity";
	
	NativeLibrary nativeLib = new NativeLibrary();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		File _getFilesDir 			= this.getFilesDir();
		
		Log.d( LOG_TAG, "getFilesDir : " + _getFilesDir.getAbsolutePath());
		Log.d( LOG_TAG, "nativeLibraryDir : " + this.getApplicationInfo().nativeLibraryDir );
		
		nativeLib.init( this );
		nativeLib.runDex2Oat( getCacheDir().getAbsolutePath() );
	}
}
