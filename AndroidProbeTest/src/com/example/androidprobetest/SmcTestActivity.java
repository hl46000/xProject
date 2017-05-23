package com.example.androidprobetest;

import android.os.Bundle;

public class SmcTestActivity extends BaseActivity {
	
	static {
		System.loadLibrary( "smc" );
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_activity_main);
	}
}
