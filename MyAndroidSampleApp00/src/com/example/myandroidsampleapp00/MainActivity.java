package com.example.myandroidsampleapp00;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	NativeLibrary nativeLib = new NativeLibrary();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File _getFilesDir 			= this.getFilesDir();
		
		Log.d( "TEST", "getFilesDir : " + _getFilesDir.getAbsolutePath());
		Log.d( "TEST", "nativeLibraryDir : " + this.getApplicationInfo().nativeLibraryDir );
		
		
		nativeLib.init( this );
	}
}
