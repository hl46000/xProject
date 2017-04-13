package com.purehero.ftp.server;

/**
 * Created by MY on 2017-02-25.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

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
