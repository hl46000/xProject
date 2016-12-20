package com.purehero.apk.manager;

import java.io.Closeable;
import java.io.IOException;

import android.util.Log;

public class G {

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
	 * @param msg
	 */
	public static void log( String msg ) {
		Log.d( "ApkManager", msg );
	}
}
