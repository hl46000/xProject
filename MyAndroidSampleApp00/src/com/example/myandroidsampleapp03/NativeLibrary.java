package com.example.myandroidsampleapp03;

import android.content.Context;

public class NativeLibrary {
	public native void init( Context context );
	public native void runDex2Oat(String cachePath );
	
	static {
		System.loadLibrary( "sample03" );
	}
}
