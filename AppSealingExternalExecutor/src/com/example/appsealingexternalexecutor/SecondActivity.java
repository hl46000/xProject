package com.example.appsealingexternalexecutor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		int btnIDs[] = { R.id.btnBack };
		for( int id : btnIDs ) {
			Button btn = ( Button ) this.findViewById( id );
			if( btn == null ) continue;
			
			btn.setOnClickListener( this );
		}
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnBack 	: this.onBackPressed(); break;
		}
		
	}
	
}
