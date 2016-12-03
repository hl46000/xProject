package com.example.myandroidsampleapp00;

import android.content.Context;

public class NativeLibrary {
	public native void init( Context context );
	
	static {
		System.loadLibrary( "sample00" );
	}
}
