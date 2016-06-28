package com.inka.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ApkSign {
	private final String VERSION = "1.0.0";
	private File apksign_path = null;
	
	public ApkSign() {
		apksign_path = new File( "E:\\SDK\\apksign");
		
		if( !new File( apksign_path, "signapk.jar" ).exists()) {
			G.errLog( "ERROR : invalid apksign root path" );
			System.exit( -1);
		}
	}
	
	/**
	 * 해당 기능에 대한 정보를 출력 합니다. 
	 */
	public void print_info() {
		G.log( "" );
		G.log( "=================================================" );
		G.log( "APK Signer [Version %s]", VERSION );
		G.log( "Copyright (c) 2015 Purehero. All rights reserved." );
		G.log( "=================================================" );
	}
	
	/**
	 * APK File 을 단말에 설치 가능하도록 특정 Key을 이용하여 서명을 한다. 성공 시 서명된 APK File 객체 반환, 실패 시에 null 반환
	 * 
	 * @param apkFile
	 * @return
	 */
	public File sign( File apkFile ) {
		String command = null;
		
		print_info();
		
		if( !apkFile.exists()) {
			G.errLog( "ERROR : invalid apk file path( not exist file '%s' )", apkFile.getAbsolutePath() );
			return null;
		}
						
		String outputFilename = apkFile.getName();
		if( outputFilename.contains( "unsigned" )) {
			outputFilename = outputFilename.replace( "unsigned", "signed");
		} else {
			outputFilename = "signed_" + outputFilename; 
		}
		outputFilename = outputFilename.replace( ".apk.decode", "");
		
		File outputFile = new File( apkFile.getParentFile(), outputFilename );
		outputFile.delete();
		
		G.log( "Src : '%s'", apkFile.getAbsolutePath() );
		G.log( "Dst : '%s'", outputFile.getAbsolutePath() );
		G.log( "=================================================" );
		G.log( "" );
		
		final String cmd_format = "java@>>---jar@>>--%s\\signapk.jar@>>--%s\\testkey.x509.pem@>>--%s\\testkey.pk8@>>--%s@>>--%s";
		if( G.isWindowsOS()) {
			command = String.format( cmd_format, 
					apksign_path.getAbsolutePath(), apksign_path.getAbsolutePath(), apksign_path.getAbsolutePath(), 
					apkFile.getAbsolutePath(), outputFile.getAbsolutePath() );
		} else {
			command = String.format( cmd_format.replace( "\\", "/"),					
					apksign_path.getAbsolutePath(), apksign_path.getAbsolutePath(), apksign_path.getAbsolutePath(), 
					apkFile.getAbsolutePath(), outputFile.getAbsolutePath() );
		}
				
		G.log( command.replace("@>>--", " "));
		
		try {
			ProcessBuilder builder = new ProcessBuilder( command.split("@>>--") );
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			InputStreamReader istream = new  InputStreamReader(process.getInputStream(), Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(istream);
			
			String line;
			while ((line = br.readLine()) != null) {
				G.log( line.replace("%", "%%"));
			}
			process.waitFor();
			br.close();
			
			return outputFile.exists() ? outputFile : null;			
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return null;
	}
}
