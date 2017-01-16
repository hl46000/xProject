package com.purehero.qr.reader;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity);

		int IDs[] = { R.id.btnCopy, R.id.btnOpenLink, R.id.btnClose };
		for( int id : IDs ) {
			Button btn = ( Button ) findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		
		Intent intent = getIntent();
		if( intent == null ) {
			finish();
		}
		
		TextView tvTitle 	= ( TextView ) findViewById( R.id.textTitle );
		if( tvTitle != null ) {
			tvTitle.setText( intent.getStringExtra("title") );
		}
		TextView tvContent 	= ( TextView ) findViewById( R.id.textContents );
		if( tvContent != null ) {
			tvContent.setText( intent.getStringExtra("content") );
		}
		
		String format = intent.getStringExtra( "format" );
		if( format != null ) {
			Log.d( "QReader" , "format : " + format );
		}
		Set<String> keys = intent.getExtras().keySet();
		for( String key : keys ) {
			Log.d( "QReader" , "key : " + key );
		}
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnCopy :
		case R.id.btnOpenLink :
		case R.id.btnClose :
			this.setResult( 200 );
			this.finish();
			break;
		}
		
	}

}
