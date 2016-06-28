package com.inka.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class NdkBuild {
	private final String VERSION = "1.0.0";
	private File ndk_path = null;
	
	public NdkBuild() {
		ndk_path = new File( "E:\\SDK\\AndroidSDK\\android-ndk-r10d");
		
		if( !new File( ndk_path, "ndk-build" ).exists()) {
			G.errLog( "ERROR : invalid NDK root path" );
			System.exit(-1);
		}
	}
	
	/**
	 * 해당 기능에 대한 정보를 출력 합니다. 
	 */
	public void print_info() {
		G.log( "" );
		G.log( "=================================================" );
		G.log( "NDK Builder [Version %s]", VERSION );
		G.log( "Copyright (c) 2015 Purehero. All rights reserved." );
		G.log( "=================================================" );
		G.log( "" );
	}
	
	
	/**
	 * Android project 을 ndk build 을 수행한다. 
	 * 
	 * @param androidProjectFolder
	 * @return
	 */
	public boolean build( File androidProjectFolder, boolean rebuild ) {
		print_info();
		
		File jni_folder = new File( androidProjectFolder, "jni" );
		if( !jni_folder.exists()) {
			G.errLog( "ERROR : not supported ndk-build project( not exist jni folder in project path )" );
			return false;
		}
		
		File androidMK = new File( jni_folder, "Android.mk" );
		if( !androidMK.exists()) {
			G.errLog( "ERROR : not supported ndk-build project( not exist Android.mk file in jni folder )" );
			return false;
		}
		
		if( rebuild ) {
			File obj_folder = new File( androidProjectFolder, "obj" );
			try {
				FileUtils.deleteDirectory( obj_folder );
			} catch (IOException e1) {
			}
		}
		
		String command = null;
		if( G.isWindowsOS()) {
			command = String.format( "%s\\ndk-build.cmd@>>---C@>>--%s", ndk_path.getAbsolutePath(), androidProjectFolder.getAbsolutePath());
		} else {
			command = String.format( "%s/ndk-build@>>---C@>>--%s", ndk_path.getAbsolutePath(), androidProjectFolder.getAbsolutePath());
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
			
		} catch( Exception e ) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
