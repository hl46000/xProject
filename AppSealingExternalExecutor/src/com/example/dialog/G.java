package com.example.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class G {
	public static final int DIALOG_BUTTON_ID_YES 	= -1;
	public static final int DIALOG_BUTTON_ID_NO 	= -2;
	
	private static boolean debuggable = false;
	public static void init( Context context ) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
			debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
		} catch (NameNotFoundException e) {
			/* debuggable variable will remain false */
		}
	}
	
	private static String TAG = "BluetoothShare";
	public static void Log() { 
		Log("");
	}
	public static void Log( String format, Object ... args ) {
		if( !debuggable ) return;
		Log.d( TAG, buildLogMsg( String.format( format, args )));
	}
	public static void Log( String msg ) {
		if( !debuggable ) return;
		Log.d( TAG, buildLogMsg( msg ));
	}
	private static String buildLogMsg(String message) { 
		StackTraceElement ste = Thread.currentThread().getStackTrace()[4]; 
		StringBuilder sb = new StringBuilder(); 
		sb.append("["); 
		sb.append(ste.getFileName().replace(".java", "")); 
		sb.append("::"); 
		sb.append(ste.getMethodName()); 
		sb.append("]"); 
		sb.append(message); 
		return sb.toString(); 
	}

	
	
	
	
	
	public static void waitDialog( final Activity activity, final String title, final String message, final ProgressRunnable runnable ) {
		new progressDialogTask( activity, title, message, ProgressDialog.STYLE_SPINNER, runnable ).execute();
	}
	
	/**
	 * @param activity
	 * @param title
	 * @param message
	 * @param runnable
	 */
	public static void progressDialog( final Activity activity, final String title, final String message, final ProgressRunnable runnable ) {
		new progressDialogTask( activity, title, message, ProgressDialog.STYLE_HORIZONTAL, runnable ).execute();
	}
	
	private static class progressDialogTask extends AsyncTask<Void, Void, Void> {
        final ProgressDialog asyncDialog;
        final ProgressRunnable runnable;
        
        public progressDialogTask( Activity activity, String title, String message, int dialogType, ProgressRunnable runnable ) {
        	asyncDialog 	= new ProgressDialog( activity );
        	this.runnable	= runnable;
        	
        	asyncDialog.setProgressStyle(dialogType);
        	if( title != null ) asyncDialog.setTitle( title );
        	if( message != null ) asyncDialog.setMessage( message );
        }
        
        @Override
        protected void onPreExecute() {
            asyncDialog.show(); 
            super.onPreExecute();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
        	runnable.run( asyncDialog );
        	return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
