package com.example.hooking;

import java.io.File;

import com.example.myandroidsampleapp00.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	final String LOG_TAG = "TEST_Activity";
	
	NativeLibrary nativeLib = new NativeLibrary();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_hooking);
		
		File _getFilesDir 			= this.getFilesDir();
		
		Log.d( LOG_TAG, "getFilesDir : " + _getFilesDir.getAbsolutePath());
		Log.d( LOG_TAG, "nativeLibraryDir : " + this.getApplicationInfo().nativeLibraryDir );
		
		nativeLib.init( this );
		
		int btnIDs [] = { R.id.btnHooking, R.id.btnTest };
		for( int id : btnIDs ) {
			Button btn = ( Button ) findViewById(id);
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
	}

	@Override
	public void onClick(View view) {
		switch( view.getId()) {
		case R.id.btnHooking :
			nativeLib.hooking( this );
			break;
		case R.id.btnTest :
			nativeLib.open_test( this );
			System.loadLibrary("sample");
			break;
		}
		
	}
}
