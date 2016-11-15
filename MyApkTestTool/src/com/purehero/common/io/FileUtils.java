package com.purehero.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	
	/**
	 * 리소스 내의 파일을 parent 폴더의 위치로 이동 시키고 파일의 경로를 반환한다. 
	 * 
	 * @param clsLoader
	 * @param resName
	 * @param parent
	 * @return
	 */
	public static File extractFileFromJar( ClassLoader clsLoader, String resName, File parent ) {
		File inFile		= new File( resName );
		File outFile 	= new File( parent, inFile.getName());
		if( outFile.exists() ) return outFile;
		
		outFile.getParentFile().mkdirs();
		
		InputStream is = clsLoader.getResourceAsStream( resName );
		if( is == null ) return null;
		
		FileUtils.fileWrite( is, outFile );
		
		return outFile;
	}
	
	
	/**
	 * is 의 스트림을 outFile 로 기록한다.<br>
	 * 기록이 완료되면 is 스트림은 close 시킨다.
	 *  
	 * @param is
	 * @param outFile
	 * @return outFile 에 기록된 byte 수를 반환 합니다.
	 */
	public static int fileWrite(InputStream is, File outFile) {
		int ret = 0;
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( outFile );
			
			byte buffer[] = new byte[102400];
			int nRead = 0;
			
			while(( nRead = is.read( buffer )) > 0 ) {
				ret += nRead;
				
				fos.write( buffer, 0, nRead );
			}
						
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if( fos != null ) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
