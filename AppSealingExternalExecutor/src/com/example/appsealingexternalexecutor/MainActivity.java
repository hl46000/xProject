package com.example.appsealingexternalexecutor;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	static {
		System.loadLibrary("AppSealingExternalExecutor");
	}
}
