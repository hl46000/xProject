package com.example.run.executor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

public class ExternalExecutor {
	final String LOG_TAG = "AppSealing_ExternalExecutor";
	
	private Context context;
	
	/**
	 * @param context
	 */
	public ExternalExecutor(Context context) {
		super();
		this.context = context;
	}

	public int extractFile( String name, File dstFile ) throws Exception {
		FileOutputStream fos = new FileOutputStream( dstFile ); 
		File exe_file = extractFileFromAssets( name, fos );
		
		if( exe_file == null ) {
			Log.e( LOG_TAG, String.format( "Failed to extract '%s' file from assets", name ));
			return -100;
		}
		
		try {
			changePermissons( exe_file, 0777 );
		} catch (Exception e) {
			Log.e( LOG_TAG, String.format( "Failed to changePermissons '%s' file", name ));
			return -200;
		}
		
		return 0;
	}
	
	public int executeFileFromAssets( String name ) throws Exception { return executeFileFromAssets( name, null ); }
	public int executeFileFromAssets( String name, String [] args ) throws Exception {
		File exe_file = extractFileFromAssets( name, context.openFileOutput( name, 0 ));		
		if( exe_file == null ) {
			Log.e( LOG_TAG, String.format( "Failed to extract '%s' file from assets", name ));
			return -100;
		}
		
		try {
			changePermissons( exe_file, 0777 );
		} catch (Exception e) {
			Log.e( LOG_TAG, String.format( "Failed to changePermissons '%s' file", name ));
			return -200;
		}
		
		return executeFile( exe_file, args );		
	}    

	
	/**
	 * @param file
	 */
	private int executeFile ( File file, String[] args ) {
    	Log.d( LOG_TAG, String.format( "executeFile : %s ", file.getAbsolutePath() ));
    	
    	int return_code = -1;
    	String[] cmd_args = null; 
    	
    	if( args != null ) {
    		cmd_args = new String[ 1 + args.length ];
    		for( int i = 0; i < args.length; i++ ) cmd_args[i+1] = args[i];
    	} else {
    		cmd_args = new String[ 1 ];
    	}
    	cmd_args[0] = file.getAbsolutePath();
    	
    	try {
			Process process = Runtime.getRuntime().exec( cmd_args );
			return_code = process.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
			return -300;
		}
    	
    	return return_code;
	}
	
	
	/**
	 * 파일의 권한을 변경해 줍니다. 
	 * 
	 * @param path
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	private int changePermissons( File path, int mode) throws Exception {
		Class<?> fileUtils = Class.forName("android.os.FileUtils");
		Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
		
		return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
	 }
	
	/**
	 * assets 에 포함된 filename 을 파일을 /data/data/<package name>/filename 에 추출한다. 
	 * 
	 * @param filename
	 * @return
	 */
	private File extractFileFromAssets( String filename, FileOutputStream fos ) {
		File ret = null;
		InputStream is = null;
		
		try {
			is = context.getAssets().open( filename );			
			
			byte buff [] = new byte[1024];
			
			int nRead = 0;
			while(( nRead = is.read( buff, 0, 1024)) > 0 ) {
				fos.write( buff, 0, nRead );
			}
			ret = new File( context.getFilesDir(), filename );
			
		} catch ( Exception e) {
			e.printStackTrace();
			
		} finally {
			if( is != null ) {
				try { is.close(); } catch (IOException e) {}
			}
			
			if( fos != null ) {
				try { fos.close(); } catch (IOException e) {}
			}
		}
		
		return ret;
	}
}