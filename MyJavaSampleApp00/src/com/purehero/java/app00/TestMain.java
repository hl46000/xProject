package com.purehero.java.app00;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TestMain {
	public static void main(String[] args) throws IOException {
		File file = new File("x:\\libmain.so");
		byte buffer[] = FileUtils.readFileToByteArray( file );
		
		for( int i = 4096; i < buffer.length - ( 4096 * 2 ); i++ ) {
			buffer[i] = ( byte )( buffer[i] ^ 0xaa );
		}
		
		FileUtils.writeByteArrayToFile( file, buffer );
	}
}
