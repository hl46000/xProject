package com.example.hooking;

import android.content.Context;

public class NativeLibrary {
	public native void init( Context context );
	
	public native void hooking( Context context );
	public native void open_test( Context context );
		
	static {
		System.loadLibrary( "hooking" );
	}
}
