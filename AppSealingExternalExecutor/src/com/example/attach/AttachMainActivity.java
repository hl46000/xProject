package com.example.attach;

import com.example.appsealingexternalexecutor.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AttachMainActivity extends Activity implements OnClickListener {
	static {
		System.loadLibrary("attach");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int btnIDs[] = { R.id.btnLauncherActivity, R.id.btnNoticeActivity, R.id.btnRepeatNotification, R.id.btnSecondActivity };
		for( int id : btnIDs ) {
			Button btn = ( Button ) this.findViewById( id );
			if( btn == null ) continue;
			
			btn.setOnClickListener( this );
		}
	}

	@Override
	public void onClick(View arg0) {
		Toast.makeText( this, String.format( "clicked %d", arg0.getId()), Toast.LENGTH_SHORT ).show();
	}
	
}
