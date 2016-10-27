package com.purehero.common.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
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
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public boolean save() {
		return save( path );
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String getValue( String key ) {
		if( properties == null ) return null; 
		return properties.getProperty(key);		
	}
	
	/**
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
	 * @return
	 */
	public String getPropertyFilePath() {
		return path;
	}
}
