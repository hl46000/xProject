package com.example.myandroidsampleapp;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	final String LOG_TAG = "TEST_Activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
