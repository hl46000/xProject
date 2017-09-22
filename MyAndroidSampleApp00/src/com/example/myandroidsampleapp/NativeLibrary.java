package com.example.myandroidsampleapp;

import android.content.Context;

public class NativeLibrary {
	static {
		System.loadLibrary( "sample02" );
	}
	
	public native void init( Context context );
}
