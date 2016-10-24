package com.purehero.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainClass implements AutoCloseable {
	@SuppressWarnings("resource")
	public static void main(String[] args) { new MainClass(); }
	
	ADB adb = new ADB();
	public MainClass() {
		ClassLoader clsLoader = getClass().getClassLoader();
		
		String adbPath = checkPath( clsLoader, "adb/adb.exe" );
		adb.Initialize( adbPath );
		
		System.out.println("Start()");
	}

	@Override
	public void close() throws Exception {
		adb.Release();
		System.out.println("Close()");
	}
	
	/**
	 * 리소스 내의 실행 파일을 실행할 수 있는 위치로 이동 시키고 파일의 경로를 반환한다. 
	 * 
	 * @param clsLoader
	 * @param resName
	 * @return
	 */
	private String checkPath( ClassLoader clsLoader, String resName ) {
		File inFile		= new File( resName );
		File outFile 	= new File( GetTempPath(), inFile.getName());
		if( outFile.exists() ) return outFile.getAbsolutePath();
		
		outFile.getParentFile().mkdirs();
		
		InputStream is = clsLoader.getResourceAsStream( resName );
		if( is == null ) return "";
		
		fileWrite( is, outFile );
		
		return outFile.getAbsolutePath();
	}
	
	private String GetTempPath() {
		return "c:\\temp\\atm_v3";
	}
	
	/**
	 * is 의 스트림을 outFile 로 기록합니다. <br> 기록이 완료되면 is 스트림은 close 시킴니다.
	 *  
	 * @param is
	 * @param outFile
	 * @return outFile 에 기록된 byte 수를 반환 합니다. 
	 */
	private int fileWrite(InputStream is, File outFile) {
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
