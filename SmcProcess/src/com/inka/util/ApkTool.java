package com.inka.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

public class ApkTool {
	private final String VERSION = "1.0.0";
	private File apktool_path = null;
	//private String frameWorkPath = null;
	
	public ApkTool() {
		apktool_path = new File( "E:\\SDK\\apktools");
		
		if( !new File( apktool_path, "apktool.jar" ).exists()) {
			G.errLog( "ERROR : invalid apktool path" );
			System.exit( -1);
		}
	}
		
	/**
	 * �ش� ��ɿ� ���� ������ ��� �մϴ�. 
	 */
	public void print_info() {
		G.log( "" );
		G.log( "=================================================" );
		G.log( "APK Tool [Version %s]", VERSION );
		G.log( "Copyright (c) 2015 Purehero. All rights reserved." );
		G.log( "=================================================" );		
	}
	
	/**
	 * �Է¹��� APK ������ APKTOOL�� �̿��Ͽ� decode �ϰ� decoded �� ������ File ��ü�� ��ȯ �Ѵ�. ���� �� null �� ��ȯ�ȴ�. 
	 * 
	 * @param apkFile
	 * @return
	 */
	public File decode( File apkFile ) {
		String apkToolParameter = "";
		//if (shouldDecompileDex == false) {
		//   apkToolParameter += "@>>---s";
		//}
				
		File frame_path = new File( apktool_path, "framework" );
		frame_path.delete();
		
		File outDir = new File( apkFile.getParentFile(), apkFile.getName() + ".decode" );
		try {
			FileUtils.deleteDirectory( outDir );
		} catch (IOException e1) {			
		}
		
		print_info();
		G.log( "Decoding" );
		G.log( "Src : '%s'", apkFile.getAbsolutePath() );
		G.log( "Dst : '%s'", outDir.getAbsolutePath() );
		G.log( "=================================================" );
		G.log( "" );
		
		String strApkTools = String.format( "d%s@>>---f@>>----frame-path@>>--%s@>>--%s@>>---o@>>--%s", 
				apkToolParameter, frame_path.getAbsolutePath(), apkFile.getAbsolutePath(), outDir.getAbsolutePath() );
		try {
			apkToolRun( strApkTools );
		} catch( Exception e ) {
			e.printStackTrace();
			outDir = null;
			
		} finally {
			frame_path.delete();
		}
		
		G.log( "=================================================" );
		G.log( "" );
		
		return outDir;
	}
	
	
	/**
	 * APKTOOL�� decode �� ������ File ��ü�� �Է¹޾Ƽ� APKTOOL�� �̿��Ͽ� build �ϰ� build�� ������ APK ������ File ��ü�� ��ȯ�Ѵ�. <br>
	 * ���� �ÿ� null �� ��ȯ�ȴ�. 
	 * 
	 * @param decodedFolder
	 * @return
	 */
	public File build( File decodedFolder ) {
		File outApkPath = new File( decodedFolder.getParentFile(), decodedFolder.getName() + ".apk" );
		outApkPath.delete();
		
		print_info();
		G.log( "Building" );
		G.log( "Src : '%s'", decodedFolder.getAbsolutePath() );
		G.log( "Dst : '%s'", outApkPath.getAbsolutePath() );
		G.log( "=================================================" );
		G.log( "" );
		
		String strApkTools = null;
		
		File frame_path = new File( apktool_path, "framework" );
		frame_path.delete();
		
		if( G.isWindowsOS()) {
			strApkTools = String.format( "b@>>---a@>>--%s/aapt.exe@>>--%s@>>---o@>>--%s", 
					apktool_path.getAbsolutePath(), decodedFolder.getAbsolutePath(), outApkPath.getAbsolutePath() );
		} else {
			strApkTools = String.format( "b@>>---a@>>--%s/aapt@>>----frame-path@>>--%s@>>--%s@>>---o@>>--%s", 
					apktool_path.getAbsolutePath(), frame_path.getAbsolutePath(), decodedFolder.getAbsolutePath(), outApkPath.getAbsolutePath() );
		}
		
		try {
			apkToolRun( strApkTools );
		} catch( Exception e ) {
			e.printStackTrace();
			outApkPath = null;
			
		} finally {
			frame_path.delete();			
		}		
		
		G.log( "=================================================" );
		G.log( "" );
		
		return outApkPath;
	}
	
	/**
	 * APKTool �� ���� ���Ѽ� ��µǴ� ���ڿ� ���� ���� �ش�. 
	 * 
	 * @param params apk tool�� ���� ��Ű�� ���� parameter ��
	 * @return apk tool ���� ��� ���ڿ�
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected synchronized String apkToolRun( String params ) throws IOException, InterruptedException {
		String command = String.format( "java@>>---jar@>>--%s/apktool.jar@>>--%s", apktool_path, params );
		
		if( G.isWindowsOS()) 
		{
			command = "cmd@>>--/c@>>--" + command;
		}
		
		ProcessBuilder builder = new ProcessBuilder( command.split("@>>--") );
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		String ret = null;
		
		InputStreamReader istream = new  InputStreamReader(process.getInputStream());
		BufferedReader br = new BufferedReader(istream);
		
		String line;
		while ((line = br.readLine()) != null){
			if( ret == null ) {
				ret = line;
			} else {
				ret += "\n";
				ret += line;
			}
			
			G.log( line );
			/*
			if( line.startsWith( "I: Loading resource table from file:" )) {
				frameWorkPath = line.substring( "I: Loading resource table from file:".length() ).trim();
			}
			*/
		}
		process.waitFor();
		br.close();
		
		if( ret.contains( "Exception" ) || ret.contains( "Can't create")) {			
			G.errLog( "apktool result has Exception!!" );
			throw new IOException( String.format( "%s\napktool result has Exception!!", ret ));
		}
		
		return ret;
	}
}

