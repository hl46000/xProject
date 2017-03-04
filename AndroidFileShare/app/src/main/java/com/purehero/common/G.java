package com.purehero.common;

/**
 * Created by MY on 2017-02-25.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

public class G {
    public static final int DIALOG_BUTTON_ID_YES 	= -1;
    public static final int DIALOG_BUTTON_ID_NO 	= -2;

    private static boolean debuggable = true;
    public static void init( Context context ) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
			/* debuggable variable will remain false */
        }
    }

    private static String TAG = "FileManager";
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
                if( no_string_res == -1 || yes_string_res == -1 ) {
                    alt_bld.setMessage( message ).setCancelable( false )
                            .setNegativeButton( "NO", listener )		// -2
                            .setPositiveButton( "YES", listener );	// -1
                } else {
                    alt_bld.setMessage( message ).setCancelable( false )
                            .setNegativeButton( no_string_res, listener )		// -2
                            .setPositiveButton( yes_string_res, listener );	// -1
                }

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
                if( no_string_res == -1 || yes_string_res == -1 ) {
                    alt_bld.setMessage(message).setCancelable(false)
                            .setNegativeButton( "NO", listener)        // -2
                            .setPositiveButton( "YES", listener);    // -1
                } else {
                    alt_bld.setMessage(message).setCancelable(false)
                            .setNegativeButton( no_string_res, listener)        // -2
                            .setPositiveButton( yes_string_res, listener);    // -1
                }
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
    public static int no_string_res = -1;
    public static int yes_string_res = -1;

    public static String getTextInputDialogResult() {
        return __input.getText().toString();
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
