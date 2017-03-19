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
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class G {
    public static final int DIALOG_BUTTON_ID_YES 	= -1;
    public static final int DIALOG_BUTTON_ID_NO 	= -2;

    public static boolean debuggable = true;
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
        try {
            sb.append(ste.getFileName().replace(".java", ""));
            sb.append("::");
            sb.append(ste.getMethodName());
        } catch( Exception e ) {}
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }


    /**
     * @param closeable
     */
    public static void safe_close( Closeable closeable ) {
        if( closeable != null ) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 확인 용 Dialog 을 띄워준다.
     *
     * @param
     * @param
     * @param
     * @param res_icon
     * @param listener
     */
    public static void confirmDialog( final Activity activity, int title_res_id, int message_res_id, final int res_icon, final DialogInterface.OnClickListener listener ){
        confirmDialog( activity, title_res_id, activity.getString( message_res_id ), res_icon, listener );
    }

    public static void confirmDialog( final Activity activity, int title_res_id, String message, final int res_icon, final DialogInterface.OnClickListener listener ){
        confirmDialog( activity, activity.getString( title_res_id ), message, res_icon, listener );
    }

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
        __input = new EditText( activity );
        //__input.setHint( hint );
        __input.setText( hint );
        __input.selectAll();
        __input.setMaxLines(1);
        __input.setHorizontallyScrolling(true);
        __input.setMovementMethod(new ScrollingMovementMethod());

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
    public static boolean progressDialogCanceled = false;
    public static void progressDialog( final Activity activity, int title_res_id, final String message, final ProgressRunnable runnable ) {
        String title = activity.getString( title_res_id );
        progressDialog( activity, title, message, runnable );
    }
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
            progressDialogCanceled = false;
            runnable.run( asyncDialog );
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
