package com.example.androidprobetest;

import android.os.Bundle;

public class AntiSpeedHackActivity extends BaseActivity {
	
	static {
		System.loadLibrary( "ash" );
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_activity_main);
	}
}
