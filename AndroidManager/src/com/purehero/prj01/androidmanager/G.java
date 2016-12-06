package com.purehero.prj01.androidmanager;

import java.io.Closeable;
import java.io.IOException;

public class G {

	public static void safe_close( Closeable closeAble ) 
	{
		if( closeAble != null ) {
			try {
				closeAble.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
