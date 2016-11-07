package com.purehero.common.io;

import java.net.URL;

public class PathUtils {
	/**
	 * 
	 * @param runnableClass
	 * @return
	 */
	public static String GetCurrentPath( Object runnableClass ) {
		try {
			Class<?> _class 			= runnableClass.getClass();
			ClassLoader _classLoader 	= _class.getClassLoader();
			URL _resource				= _classLoader.getResource("path.txt");
			String jarDir 				= _resource.getPath();
			
			if( System.getProperty( "os.name" ).contains( "Window" )) {
				if( jarDir.startsWith("/")) jarDir = jarDir.substring(1);
			}
			
			return jarDir;
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
}
