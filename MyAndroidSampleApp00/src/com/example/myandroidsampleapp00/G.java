package com.example.myandroidsampleapp00;

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
