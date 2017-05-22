package com.covault.appsec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CovaultMessagePanel implements OnClickListener {
	public static final int BTN_ID_CLOSE = 0x45678;
	WindowManager WINDOW_MANAGER = null;
	
	TextView tvMessage = null;
	Button btnClose = null;
	
	LinearLayout layout = null;
	Context context = null;
	String title = null;
	
	public final static int HACKING_ALERT = 1;
	public final static int VM_ALERT = 2;
	public final static int ROOTING_ALERT = 3;
	
	public CovaultMessagePanel( Context context ) {
		this.context = context;
	}
	
	public LinearLayout init( String title ) {
		this.title   = title;
		if( WINDOW_MANAGER == null) {
			WINDOW_MANAGER = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		
		layout = new LinearLayout( context );
		
		GradientDrawable bg = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{ 0xDFB7D3FF, 0xDFDAEEFF});
		bg.setStroke( 10, Color.RED );
		bg.setGradientRadius( 270 );
		bg.setCornerRadius( 20 );
		layout.setBackgroundDrawable( bg );
		//this.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 240 ) );
		layout.setPadding( 10, 10, 10, 10);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView tvTitle = new TextView( context );
		tvTitle.setText( title );
		tvTitle.setGravity( Gravity.CENTER_VERTICAL );
		tvTitle.setTextColor( 0xFF000000 );
		tvTitle.setEllipsize( TruncateAt.END );
		
		LinearLayout.LayoutParams tvTitleParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		tvTitleParams.leftMargin = tvTitleParams.rightMargin = 5;
				
		LinearLayout messageLayout = new LinearLayout( context );
		LinearLayout.LayoutParams tvMessageParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );
		tvMessageParams.topMargin = tvMessageParams.bottomMargin = tvMessageParams.leftMargin = tvMessageParams.rightMargin = 5;
				
		tvMessage = new TextView( context );
		tvMessage.setText( "Message" );
		tvMessage.setLayoutParams( tvMessageParams );
		tvMessage.setGravity( Gravity.CENTER );
		tvMessage.setTextColor( 0xFF0F0F0F );
		tvMessage.setBackgroundColor( 0xF0F0F0FF );
		messageLayout.addView( tvMessage );
						
		btnClose = new Button( context );
		btnClose.setText( "   OK   " );
		btnClose.setId( BTN_ID_CLOSE );
		btnClose.setOnClickListener( this );
		btnClose.setVisibility( View.VISIBLE );
		
		layout.addView( tvTitle, tvTitleParams );
		layout.addView( messageLayout, new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, 0, 10 ) );
		layout.addView( btnClose, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		
		return layout;
	}

	

	/**
	 * 
	 */
	public void showPanel( int type , String msg) {
//public void showPanel( CovaultService covaultService , int type , String msg) {
//		if( covaultService == null ) {
//			Log.e( "Covault", "showPanel : message is null" );
//			return;
//		}
		if( tvMessage != null ) {
			//tvMessage.setText( covaultService.readMessage() );
			String message = null;
			switch (type) {
				case HACKING_ALERT :
					message = "App was closed by the following reasons: improper launch or attack was detected.";
					if (msg != null){
						message = String.format("Risk factor has been detected! Please delete %s ( %s ) %s  related app and try launching the app again after rebooting the device.",System.getProperty("line.separator"),msg,System.getProperty("line.separator"));
					}
					break;
				case VM_ALERT :
					message = "Launching the app on VM is blocked.";
					break;
				case ROOTING_ALERT : 
					message =  "Launching the app on rooted device is blocked." ;
					break;
			}
			tvMessage.setText( message );
		}
		//DisplayMetrics disMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
		//int mainLayout_Width = (int) (disMetrics.widthPixels * 0.8);
		//int mainLayout_Height = (int) (disMetrics.heightPixels * 0.1);
		//int mainPosition_X = (int) ((disMetrics.widthPixels/2) - (mainLayout_Width/2));
		//int mainPosition_Y = (int) ((disMetrics.heightPixels/2) - (mainLayout_Height/2));
		
		//Log.d( LOG_TAG, String.format("mainLayout_Width : %d", mainLayout_Width ));
		//Log.d( LOG_TAG, String.format("mainLayout_Height : %d", mainLayout_Height ));
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
				//mainLayout_Width,
				WindowManager.LayoutParams.WRAP_CONTENT,
				//mainLayout_Height*3,
				WindowManager.LayoutParams.WRAP_CONTENT,
				0,
				0,
				WindowManager.LayoutParams.TYPE_PHONE, 
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, 
				PixelFormat.TRANSLUCENT);
		
		WINDOW_MANAGER.addView( layout, mParams );
		//covaultService.clearReadMessage();
	}
		
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case BTN_ID_CLOSE :
			if( WINDOW_MANAGER != null ) {
				WINDOW_MANAGER.removeView( layout );
			}
			//context.stopService( new Intent( context, CovaultService.class ) );
			//System.exit(-1);
			break;
		}
	}
}
