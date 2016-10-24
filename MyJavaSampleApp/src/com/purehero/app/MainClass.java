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
	 * ���ҽ� ���� ���� ������ ������ �� �ִ� ��ġ�� �̵� ��Ű�� ������ ��θ� ��ȯ�Ѵ�. 
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
	 * is �� ��Ʈ���� outFile �� ����մϴ�. <br> ����� �Ϸ�Ǹ� is ��Ʈ���� close ��Ŵ�ϴ�.
	 *  
	 * @param is
	 * @param outFile
	 * @return outFile �� ��ϵ� byte ���� ��ȯ �մϴ�. 
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
