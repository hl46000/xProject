package com.example.myandroidsampleapp00;

import android.content.Context;

public class NativeLibrary {
	public native void init( Context context );
	public native void runDex2Oat();
	
	static {
		System.loadLibrary( "sample00" );
	}
}
