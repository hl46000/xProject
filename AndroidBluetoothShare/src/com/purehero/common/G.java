package com.purehero.common;

import com.purehero.bluetooth.share.R;
import com.purehero.bluetooth.share.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

public class G {
	public static final int DIALOG_BUTTON_ID_YES 	= -1;
	public static final int DIALOG_BUTTON_ID_NO 	= -2;
	
	private static String TAG = "BluetoothShare";
	public static void Log( String format, Object ... args ) {
		Log.d( TAG, String.format( format, args ));
	}
	public static void Log( String msg ) {
		Log.d( TAG, msg );
	}
	
	/**
	 * 확인 용 Dialog 을 띄워준다. 
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param res_icon
	 * @param listener
	 */
	public static void confirmDialog( final Activity activity, final String title, final String message, final int res_icon, final DialogInterface.OnClickListener listener ){
		activity.runOnUiThread( new Runnable(){
			@Override
			public void run() {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder( activity );
			    alt_bld.setMessage( message ).setCancelable( false )
			        .setNegativeButton( R.string.no, listener )		// -2
			    	.setPositiveButton( R.string.yes, listener );	// -1
			    AlertDialog alert = alt_bld.create();
			    // Title for AlertDialog
			    if( title != null ) {
			    	alert.setTitle( title );
			    }
			    // Icon for AlertDialog
			    if( res_icon > 0 ) {
			    	alert.setIcon( res_icon );
			    }
			    alert.show();
			}});
	}
	
	/**
	 * 한줄 입력을 받는 Dialog 을 띄운다. 
	 * 
	 * @param activity
	 * @param title
	 * @param message
	 * @param res_icon
	 * @param listener
	 * @return
	 */
	private static EditText __input = null;
	public static void textInputDialog( final Activity activity, final String title, final String message, String hint, final int res_icon, final DialogInterface.OnClickListener listener ) {
		__input = new EditText(activity);
		__input.setHint( hint );
		
		activity.runOnUiThread( new Runnable(){
			@Override
			public void run() {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder( activity );
				alt_bld.setView(__input);
				alt_bld.setMessage( message ).setCancelable( false )
			        .setNegativeButton( R.string.no, listener )		// -2
			    	.setPositiveButton( R.string.yes, listener );	// -1
			    AlertDialog alert = alt_bld.create();
			    // Title for AlertDialog
			    if( title != null ) {
			    	alert.setTitle( title );
			    }
			    // Icon for AlertDialog
			    if( res_icon > 0 ) {
			    	alert.setIcon( res_icon );
			    }
			    alert.show();
			}});
	}
	
	public static String getTextInputDialogResult() {
		return __input.getText().toString();
	}
}
