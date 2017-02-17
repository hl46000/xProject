package com.example.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		G.waitDialog(this, "title", "로딩 중입니다...", new ProgressRunnable(){

			@Override
			public void run( final ProgressDialog dialog) {
				for( int i = 0; i < 100; i++ ) {
					final String new_message = String.format( "로딩 중입니다...(%d/100)", i );
					MainActivity.this.runOnUiThread( new Runnable(){
						@Override
						public void run() {
							dialog.setMessage( new_message );
						}});
					
					//dialog.setProgress(i);
					try {
						Thread.sleep( 100 );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}});


		G.progressDialog( this, "title", "로딩 중입니다...", new ProgressRunnable(){

			@Override
			public void run( final ProgressDialog dialog) {
				int max = 200;
				dialog.setMax(max);
				
				for( int i = 0; i < max; i++ ) {
					dialog.setProgress(i);
					
					try {
						Thread.sleep( 60 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}});

	}

}
