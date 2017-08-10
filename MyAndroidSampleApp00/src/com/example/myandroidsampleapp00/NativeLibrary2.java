package com.example.myandroidsampleapp00;

import android.content.Context;

public class NativeLibrary2 {
	public native void init( Context context );
	
	static {
		//System.loadLibrary( "sample01" );
		System.loadLibrary( "sample02" );
	}
}
