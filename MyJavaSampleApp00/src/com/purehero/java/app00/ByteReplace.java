package com.purehero.java.app00;

import java.io.File;

import org.apache.commons.io.FileUtils;

/*
 * ���Ͽ��� ���ڿ��� ��ü�Ͽ� �ش�. 
 * 
 * <file path> <ã�� ���ڿ�> <��ü�� ���ڿ�> <���ڿ��� Encoding:UTF-8 or UTF-16LE>
 * 
 * 
 * */
public class ByteReplace {

	public static void main(String[] args) {
		try {
			if( args == null ) 		{ display_usage(); return; }
			if( args.length < 4 ) 	{ display_usage(); return; }
			
			File file = new File( args[0] );	// ��� ����
			byte content [] = FileUtils.readFileToByteArray( file ); 
			
			String src = args[1];				// ã�� ���ڿ�
			String dst = args[2];				// �ٲ� ���ڿ�
			if( src.length() != dst.length()) {
				System.err.println( String.format( "'%s' and '%s' length not matched!!", src, dst ));
				return;
			}
			
			String enc = args[3];				// ���ڿ��� encoding : UTF-8 or UTF-16
			
			byte src_bytes[] = src.getBytes( enc );
			byte dst_bytes[] = dst.getBytes( enc );
			
			int count = 0;
			int len = content.length - src_bytes.length;
			for( int i = 0; i < len; i++ ) {
				if( equals( content, i, src_bytes )) {
					System.arraycopy( dst_bytes, 0, content, i, dst_bytes.length );
					
					count++;
				}
			}
			
			FileUtils.writeByteArrayToFile( file, content );
			System.out.println( String.format( "%d replaced", count ));
			
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			
		}
	}

	private static void display_usage() {
		System.out.println( "USAGE : java -jar <this jar> <target filepath> <search string> <replace string> <string encoding>" );
		System.out.println( "ex) java -jar c:\\test.txt 1234 abcd UTF-8" );
		System.out.println( "    change string 1234 to abcd in c:\\test.txt file" );
	}
	
	private static boolean equals( byte [] a, int offset, byte [] b ) {
		int len = b.length;
		if( len > a.length - offset ) return false;
		
		boolean ret = true;
		for( int i = 0; i < len; i++ ) {
			if( a[offset+i] != b[i] ) {
				ret = false;
				break;
			}
		}
		
		return ret;
	}
}
