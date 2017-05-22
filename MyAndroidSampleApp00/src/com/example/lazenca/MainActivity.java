package com.example.lazenca;

import com.example.myandroidsampleapp00.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	final String LOG_TAG = "LazencaS";
	
	static{
		System.loadLibrary("LoadEngine");
		System.loadLibrary("LazencaS");
	}
	
	TextView tvText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvText = ( TextView ) findViewById( R.id.tvText );
		new Thread( test_thread ).start();
	}
	
	Runnable ui_runnable = new Runnable() {
		int num = 0;
		
		@Override
		public void run() {
			tvText.setText( String.valueOf( num++ ));
		}
		
	};
	
	Runnable test_thread = new Runnable() {
		
				
		@Override
		public void run() {
			try {
				while( true ) {
					Thread.sleep( 1000 );
					MainActivity.this.runOnUiThread( ui_runnable );
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
}
