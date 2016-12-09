package com.example.dexload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.myandroidsampleapp00.G;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class DexLoadApplication extends Application 
{
	final String LOG_TAG = "DEX_TEST_APPLICATION";
	
	@Override
	protected void attachBaseContext(Context base) 
	{
		Log.d( LOG_TAG, "attachBaseContext" );
		super.attachBaseContext(base);
		
		loadExternalDex( base, base.getClassLoader(), base.getAssets());
	}
	
	@Override
	public void onCreate() 
	{
		Log.d( LOG_TAG, "onCreate" );
		
		super.onCreate();
		
		makeApplication( this );
	}

	/**
	 * @param applicationContext
	 * @param classLoader
	 * @param assets
	 */
	BaseDexClassLoader baseDexClassLoader = null;
	private void loadExternalDex(Context context, ClassLoader classLoader, AssetManager assets) 
	{
		Log.d( LOG_TAG, "loadExternalDex" );
		
		File dexFile = copyToDexFromAsset( context, assets, "classes.dex" );
				
		File dexOptDir = new File( context.getCacheDir(), "dexopt" );
		if( !dexOptDir.exists()) {
			dexOptDir.mkdirs();
		}
		
		baseDexClassLoader = new DexClassLoader( 
			dexFile.getAbsolutePath(), 
			dexOptDir.getAbsolutePath(), 
			"/system/bin", 
			classLoader 
		);
		
		Object newPathList 		= getFieldOjbect( "dalvik.system.BaseDexClassLoader", baseDexClassLoader, "pathList" );
		Object existPathList 	= getFieldOjbect( "dalvik.system.BaseDexClassLoader", classLoader, "pathList" );
		
		Object newDexElements 	= getFieldOjbect( "dalvik.system.DexPathList", newPathList, "dexElements" );
		Object existDexElements	= getFieldOjbect( "dalvik.system.DexPathList", existPathList, "dexElements" );
				
		ArrayList<Object> integratedDexElements = new ArrayList<Object>();
		integratedDexElements.addAll( Arrays.asList( newDexElements ));
		integratedDexElements.addAll( Arrays.asList( existDexElements ));
		
		// 기존 클래스 로더 pathList 객체를 dexElements 객체에 새로 생성한 클래스 로더 객체의 dexElements 객체로 바꾼다
		setFieldOjbect( "dalvik.system.DexPathList", "dexElements", existPathList, integratedDexElements.toArray());
	}
	
	/**
	 * 
	 * 
	 * @param class_name
	 * @param obj
	 * @param filedName
	 * @return
	 */
	private Object getFieldOjbect( String class_name, Object obj, String filedName )
	{
		try {
			Class<?> obj_class = Class.forName( class_name );
			Field field = obj_class.getDeclaredField( filedName );
			field.setAccessible( true );
			return field.get( obj );
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void setFieldOjbect( String classname, String filedName, Object obj, Object filedVaule )
	{
		try {
			Class<?> obj_class = Class.forName( classname );
			Field field = obj_class.getDeclaredField( filedName );
			field.setAccessible( true );
			field.set( obj, filedVaule );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param context
	 * @param assets
	 * @param string
	 * @return
	 */
	private File copyToDexFromAsset(Context context, AssetManager assets, String string) {
		File dexTmpDir = new File( context.getCacheDir(), "dextmp" );
		if( !dexTmpDir.exists()) {
			dexTmpDir.mkdirs();
		}
		
		File dexFile = new File( dexTmpDir, "classes.dex");
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is 	= assets.open("classes.dex");
			fos = new FileOutputStream( dexFile );
			
			int buff_size = is.available();
			byte buff[] = new byte[buff_size];
			int nRead;
			
			while(( nRead = is.read( buff, 0, buff_size )) > 0 ) {
				fos.write( buff, 0, nRead );
			}
			fos.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
			
		} finally {
			G.safe_close( is );
			G.safe_close( fos );
		}
		
		return dexFile;
	}

	/**
	 * @param applicationContext
	 */
	private void makeApplication(Context applicationContext) 
	{
		Log.d( LOG_TAG, "makeApplication" );
		
	}
}
