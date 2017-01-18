package com.purehero.qr.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.result.ResultHandler;

public class ResultActivity extends Activity implements OnClickListener {
	
	private ResultHandler resultHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity);
		
		final int IDs[] = { R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6 };
		
		resultHandler = MainActivity.instance.getResultHandler();
		int btnCount = resultHandler.getButtonCount();
		for( int i = 0; i < btnCount; i++ ) {
			Button btn = ( Button ) findViewById( IDs[i] );
			if( btn != null ) {
				btn.setVisibility( View.VISIBLE );
				btn.setText( getString( resultHandler.getButtonText(i)) );
				btn.setOnClickListener( this );
			}
		}
		
		Button btn = ( Button ) findViewById( R.id.btnClose );
		if( btn != null ) {
			btn.setOnClickListener( this );
		}
		
		Intent intent = getIntent();
		if( intent == null ) {
			setResult( 100 );
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
	}

	@Override
	public void onClick(View arg0) {
		setResult( 100 );
		
		switch( arg0.getId()) {
		case R.id.btn1 : resultHandler.handleButtonPress( 0 ); finish(); break;
		case R.id.btn2 : resultHandler.handleButtonPress( 1 ); finish(); break;
		case R.id.btn3 : resultHandler.handleButtonPress( 2 ); finish(); break;
		case R.id.btn4 : resultHandler.handleButtonPress( 3 ); finish(); break;
		case R.id.btn5 : resultHandler.handleButtonPress( 4 ); finish(); break;
		case R.id.btn6 : resultHandler.handleButtonPress( 5 ); finish(); break;
		case R.id.btnClose : this.finish(); break;
		}
	}

}
