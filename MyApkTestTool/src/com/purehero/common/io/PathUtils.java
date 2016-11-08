package com.purehero.common.io;

import java.io.File;
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
			URL _resource				= _classLoader.getResource("path.txt");	// 실제 존재하지 않는 파일의 경로를 얻으려고 하면 null 이 반환된다. 
			String jarDir 				= _resource.getPath();
			
			if( System.getProperty( "os.name" ).contains( "Window" )) {
				if( jarDir.startsWith("/")) jarDir = jarDir.substring(1);
			}
						
			if( jarDir.startsWith("file:\\"))	jarDir = jarDir.substring( "file:\\".length());
			if( jarDir.startsWith("file:/"))	jarDir = jarDir.substring( "file:/".length());
			if( jarDir.endsWith( "path.txt")) jarDir = jarDir.replace( "path.txt", "");
						
			File file = new File( jarDir );
			if( file.isDirectory()) jarDir = file.getAbsolutePath(); 
			else jarDir = file.getParentFile().getAbsolutePath();
				
			return jarDir;
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
}
