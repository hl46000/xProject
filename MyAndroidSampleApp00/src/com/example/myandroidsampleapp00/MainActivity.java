package com.example.myandroidsampleapp00;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	final String LOG_TAG = "TEST_Activity";
	
	NativeLibrary2 nativeLib2 = new NativeLibrary2();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		nativeLib2.init( this );
	}
}
