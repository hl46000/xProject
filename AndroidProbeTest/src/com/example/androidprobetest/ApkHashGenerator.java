package com.example.androidprobetest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Base64;

/* *
 * APK 파일의 Hash(무결성) 검증을 위한 데이터를 생성해 주는 Class
 *  
 * @author purehero2
 *
 */
public class ApkHashGenerator {
	final boolean bDetailLog = false;
	
	final File apkFile;
	
	public ApkHashGenerator( File _apkFile ) throws FileNotFoundException {
		if( !_apkFile.exists()) {
			throw new FileNotFoundException( _apkFile.getAbsolutePath() );
		}
		
		apkFile = _apkFile;
	}
	
	/**
	 * APK 파일을 분석해서 APK 파일의 무결성을 확인 할 수 있는 데이터를 생성하여 반환한다. 
	 * 
	 * @return
	 */
	public String Generator() {
		String strManifestMF = null;
		if(( strManifestMF = GetManifestMF()) == null ) {
			return null;
		}
		
		HashSet<String> DigestSet = new HashSet<String>();
		
		String items[] = strManifestMF.split("\r\n\r\n"); 
		
		// items[0]
		//
		// Manifest-Version: 1.0
		// Created-By: 1.0 (Android)
		for( String item : items ) {
			if( !item.startsWith("Name: ")) continue;
			item = item.substring( "Name: ".length() );
			
			while( item.indexOf('\r') != -1 ) item = item.replace("\r", "");
			while( item.indexOf('\n') != -1 ) item = item.replace("\n", "");
			
			String token[] = item.split("SHA1-Digest: ");
			if( bDetailLog ) {
				System.out.printf( "Name: %s, SHA1-Digest: %s\n", token[0], token[1] );
			}
			
			// 혹시 걸러야 할 것들이 있으면 여기서 걸러 준다. continue 로 
			
			DigestSet.add( token[1] );
		}
		
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance( "MD5" );
			
			List<String> DigestList = asSortedList( DigestSet );
			for( String strDigest : DigestList ) {
				msgDigest.update( strDigest.getBytes() );
				if( bDetailLog ) {
					System.out.println( strDigest );
				}
			}
			
			return Base64.encodeToString( msgDigest.digest(), Base64.DEFAULT);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * APK file 에서 manifest.mf 파일의 내용을 찾아서 반환해 준다.
	 * 
	 * @return 실패 시 null, manifest.mf 파일 내용
	 */
	public String GetManifestMF() {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		try {
			fis = new FileInputStream( apkFile );
			zis = new ZipInputStream(fis);

			ZipEntry zipEntry = null;
			while(( zipEntry = zis.getNextEntry()) != null) {
				String zipEntryName = zipEntry.getName().toLowerCase( Locale.getDefault());
				if( zipEntryName.endsWith("manifest.mf")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte entryBuffer[] = new byte[ 10240 ];
					
					int nWritten = 0;
					while(( nWritten = zis.read( entryBuffer, 0, 10240 )) > 0 ) {
						baos.write( entryBuffer, 0, nWritten );
					}
					
					String ret = baos.toString();
					baos.close();
					
					return ret;
				}
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
			
		} finally {
			closeQuietly(zis);
			closeQuietly(fis);			
		}
		
		return null;
	}
	
	private void closeQuietly(InputStream input) {
		if( input != null ) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list;
	}
}