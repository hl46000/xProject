package com.purehero.bluetooth.share;

import android.util.Log;

public class G {
	private static String TAG = "ContactShare";
	public static void Log( String format, Object ... args ) {
		Log.d( TAG, String.format( format, args ));
	}
	public static void Log( String msg ) {
		Log.d( TAG, msg );
	}
}
