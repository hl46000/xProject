package com.purehero.java.app00;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class app00 {
	public static void main(String[] args) {
		new app00();
	}
	
	public app00() {
		System.out.println("Hello World!!!");
		
		try {
			String crashedThreadData = FileUtils.readFileToString( new File("c:\\workTemp\\a.txt"));
			String matchData 		 = getAppSealingCrashDetail( crashedThreadData );
			System.out.println(matchData);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * @param crashedThreadData
	 * @return
	 */
	private String getAppSealingCrashDetail(String crashedThreadData) {
		String lines [] = crashedThreadData.split( System.lineSeparator());
		for( String line : lines ) {
			if( !line.contains( "libcovault-appsec.so" )) continue;
			
			int idx = line.indexOf("[");
			if( idx != -1 ) line = line.substring( idx + 1 );
			idx = line.indexOf(" +");
			if( idx != -1 ) line = line.substring( 0, idx );
			return line.trim();
		}
		
		return "none";
	}
	
	/**
	 * 매칭 데이터에서 발생 순서대로 최초 2개의 문자열을 조합하여 반환합니다. 
	 * 
	 * @param machingData
	 * @return
	 */
	private String getCrashTypeFromMatchData(String crashedThreadData) {
		String ret = null;
		String lines [] = crashedThreadData.split("\n");
		for( String line : lines ) {
			while( line.indexOf("  ") != -1 ) line = line.replace("  ", " ");
			String token[] = line.split("\\+");
			if( token[0].startsWith( "Loaded modules:")) break;
			if( token.length < 2 ) continue;
			
			String value = token[0].trim();
			value = value.substring( value.indexOf(" ") + 1 );
			
			if( ret == null ) ret = value;
			else {
				if( ret.compareTo( value ) != 0 ) {
					ret += "::" + value;
					break;
				}
			}
		}
		return ret;
	}
}
