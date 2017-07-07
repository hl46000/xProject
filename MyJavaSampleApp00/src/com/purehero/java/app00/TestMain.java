package com.purehero.java.app00;

public class TestMain {
	public static void main(String[] args) {
		String data  = "dajflkajdfl;kjdlkjaskl;flk;a\"OSVERSION\":\"4.1.1\",adljfal;dfj;alkjflk;j\"OSVERSION\":\"4.1.2\"";
		String data1 = "dajflkajdfl;kjdlkjaskl;flk;a\"OSVERSION\" :  \"4.1.1\" ,adljfal;dfj;alkjflk;j\"OSVERSION\":\"4.1.2\"";
		String data2 = "dajflkajdfl;kjdlkjaskl;flk;a\"OSVERSION\" : \" 4.1.1 \" },adljfal;dfj;alkjflk;j\"OSVERSION\":\"4.1.2\"";
		String data3 = "dajflkajdfl;kjdlkjaskl;flk;a\"OSVERSION\" : \" 4.1.1 \" },adljfal;dfj;alkjflk;j";
		
		
		String value = getStringValue( data, "OSVERSION" );
		System.out.println( value );
		
		value = getStringValue( data1, "OSVERSION" );
		System.out.println( value );
		
		value = getStringValue( data2, "OSVERSION" );
		System.out.println( value );
		
		data = removeDuplicateKey( data, "OSVERSION" );
		System.out.println( data );
		
		data1 = removeDuplicateKey( data1, "OSVERSION" );
		System.out.println( data1 );
		
		data2 = removeDuplicateKey( data2, "OSVERSION" );
		System.out.println( data2 );
		
		data3 = removeDuplicateKey( data3, "OSVERSION" );
		System.out.println( data3 );
	}

	private static String removeDuplicateKey(String data, String str) {
		String token[] = data.split( str, 3 );
		if( token.length < 3 ) return data;
		
		return token[0] + str + token[1] + str + "2" + token[2];
	}

	private static String getStringValue(String data, String str ) {
		int idx = data.indexOf( str );
		if( idx < 0 ) return null;
		
		idx = data.indexOf( ":", idx );
		if( idx < 0 ) return null;
		
		idx = data.indexOf( "\"", idx );
		if( idx < 0 ) return null;
		
		int s = idx + 1;
		
		idx = data.indexOf( "\"", idx + 1 );
		if( idx < 0 ) return null;
		
		return data.substring( s, idx ).trim();
	}
}
