package com.inka.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class ApkBuild {
	private final String VERSION = "1.0.0";
	private File ant_path = null;
		
	public ApkBuild() {
		ant_path = new File( "E:\\SDK\\apache-ant-1.9.7");
				
		if( !new File( ant_path, "\\bin\\ant" ).exists()) {
			G.errLog( "ERROR : invalid ant root path" );
			System.exit(-1);
		}
	}
	
	/**
	 * 해당 기능에 대한 정보를 출력 합니다. 
	 */
	public void print_info() {
		G.log( "" );
		G.log( "=================================================" );
		G.log( "APK Builder [Version %s]", VERSION );
		G.log( "Copyright (c) 2015 Purehero. All rights reserved." );
		G.log( "=================================================" );
		G.log( "" );
	}
	
	/**
	 * Android project 을 Ant build 을 수행하여 생성된 APK File 객체를 반환한다. 실패 시에 null 반환
	 * 
	 * @param androidProjectFolder
	 * @return
	 */
	public File build( File androidProjectFolder ) {
		String command = null;
		
		print_info();
		
		File buildXML = new File( androidProjectFolder, "build.xml" );
		if( !buildXML.exists()) {
			G.errLog( "ERROR : invalid Android project path( could not found build.xml )" );
			return null;
		}
		
		File jniFolder = new File( androidProjectFolder, "jni" );
		if( jniFolder.exists()) {
			File androidMK = new File( jniFolder, "Android.mk" ); 
			if( androidMK.exists()) {
				
			}
		}
		
		File binFolder = new File( androidProjectFolder, "bin" );
		try {
			FileUtils.deleteDirectory( binFolder );
		} catch (IOException e1) {
		}
		
		if( G.isWindowsOS()) {
			command = String.format( "%s\\bin\\ant.bat@>>---f@>>--%s\\build.xml@>>--release", ant_path.getAbsolutePath(), androidProjectFolder.getAbsolutePath());
		} else {
			command = String.format( "%s/bin/ant@>>---f@>>--%s/build.xml@>>--release", ant_path.getAbsolutePath(), androidProjectFolder.getAbsolutePath());
		}
				
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
			
			File binList [] = binFolder.listFiles();
			for( File file : binList ) {
				if( file.isDirectory()) continue;
				if( file.getName().endsWith("-release-unsigned.apk")) {
					return file;
				}
			}
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return null;
	}
}
