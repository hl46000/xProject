package com.purehero.bluetooth.share;

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
import android.util.Log;
import android.widget.EditText;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static String TAG = "Bluetooth share";
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

    public static void copy_file(File src, File dest ) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream( src );
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[102400];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();

        } catch( IOException e ) {
            e.printStackTrace();

        } finally {
            safe_close( is );
            safe_close( os );
        }
    }
}
