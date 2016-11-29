package com.purehero.java.app00;

import java.io.File;
import java.io.IOException;

import net.dongliu.apk.parser.ApkParser;

/*
 * APK ������ package name �� launcher activity �� �̸��� ����Ѵ�. 
 * 
 * */

public class ApkInfo {
	public static void main(String[] args) {
		ApkParser p = null;
		try {
			File file = new File( args[0] );
			p = new ApkParser( file );
			String launcherActivity = p.getApkMeta().getLauncherActivityName();
			String packageName = p.getApkMeta().getPackageName();
			
			System.out.println( packageName + "," + launcherActivity );
			
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			if( p != null ) {
				try {
					p.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
