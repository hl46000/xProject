package com.purehero.atm.v3;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	final String LOG_TAG = "TEST";
	
	NativeLibrary nativeLibrary = new NativeLibrary();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		nativeLibrary.init_module();
	}
	
	
	static {
		System.loadLibrary("atm_v3");
	}
}
