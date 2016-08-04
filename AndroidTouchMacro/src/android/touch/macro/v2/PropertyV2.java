package android.touch.macro.v2;

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
public class PropertyV2 {
	Properties properties = null;
	String path = null;
	
	/**
	 * @param path
	 * @throws IOException
	 */
	public void load( String _path ) throws IOException {
		if( properties == null ) {
			properties = new Properties();
		}
		properties.clear();
		properties.load( new InputStreamReader( new FileInputStream( _path ), "UTF-8"));
		
		path = _path;
	}
	
	/**
	 * 
	 * @param path
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public boolean save( String path, String comments ) throws IOException {
		if( properties == null ) return false;
		properties.store(  new OutputStreamWriter( new FileOutputStream( path ), "UTF-8" ), comments );
		return true;
	}
	
	/**
	 * @param comments
	 * @return
	 * @throws IOException
	 */
	public boolean save( String comments ) throws IOException {
		return save( path, comments );
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
		if( properties == null ) return false;
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
