package com.purehero.common.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * Property 을 사용하기 위한 CLASS 
 * 
 * @author purehero
 *
 */
public class PropertyEx {
	Properties properties = null;
	String path = null;
	
	final String comments;
	public PropertyEx( String comments ) {
		this.comments = comments;
	}
	
	/**
	 * Property 파일을 불러온다. 
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void load( String _path ) throws IOException {
		if( properties == null ) {
			properties = new Properties();
		}
		path = _path;
		
		properties.clear();
		properties.load( new InputStreamReader( new FileInputStream( _path ), "UTF-8"));
	}
	
	/**
	 * Property 내용을 파일로 기록한다. 
	 * 
	 * @param path
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public boolean save( String path ) {
		if( properties == null ) return false;
		try {
			FileOutputStream fos = new FileOutputStream( path );
			properties.store( new OutputStreamWriter( fos, "UTF-8" ), comments );
			fos.close();
			
			return true;
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Property 내용을 파일로 기록한다. <br> load 함수로 불러온 파일로 기록한다. 
	 * 
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public boolean save() {
		return save( path );
	}
	
	/**
	 * key 에 해당하는 값을 반환한다. <br>key 에 해당하는 값이 없으면 null 이 반환된다. 
	 * 
	 * @param key
	 * @return
	 */
	public String getValue( String key ) {
		if( properties == null ) return null; 
		return properties.getProperty(key);		
	}
	
	/**
	 * 새로운 key, value 항목을 생성한다. <br> key 항목이 이미 존재하면 기존 key 값을 value 로 갱신한다. 
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setValue( String key, String value ) {
		if( properties == null ) {
			properties = new Properties();
		}
		properties.setProperty(key, value);
		return true;
	}
	
	/**
	 * load 함수로 불러온 Property 파일의 경로를 반환한다. 
	 * 
	 * @return
	 */
	public String getPropertyFilePath() {
		return path;
	}
}
