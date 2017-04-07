package com.purehero.root.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AndroidDeviceInfo {
	private static final String LOG_TAG = "AndroidDeviceInfo";
	
	/**
	 * 단말기의 정보를 반환 합니다. 
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String,String> getInfo( Context context ) 
	{
		Map<String,String> ret = new HashMap<String,String>();
		
		ret.put( "ID", Build.ID );
		ret.put( "DISPLAY", Build.DISPLAY );
		ret.put( "PRODUCT", Build.PRODUCT );
		ret.put( "DEVICE", Build.DEVICE );
		ret.put( "BOARD", Build.BOARD );
		ret.put( "CPU_ABI", Build.CPU_ABI );
		ret.put( "CPU_ABI2", Build.CPU_ABI2 );
		ret.put( "MANUFACTURER", Build.MANUFACTURER );
		ret.put( "BRAND", Build.BRAND );
		ret.put( "MODEL", Build.MODEL );
		ret.put( "BOOTLOADER", Build.BOOTLOADER );
		ret.put( "HARDWARE", Build.HARDWARE );
		ret.put( "SERIAL", Build.SERIAL );
		ret.put( "TYPE", Build.TYPE );
		ret.put( "TAGS", Build.TAGS );
		ret.put( "FINGERPRINT", Build.FINGERPRINT );
		ret.put( "USER", Build.USER );
		ret.put( "HOST", Build.HOST );
		ret.put( "RADIO", Build.getRadioVersion() );
		ret.put( "TIME", SimpleDateFormat.getInstance().format(new Date(Build.TIME)));
		ret.put( "NAME", getDeviceName( context ) );
	
		ret.put( "CODENAME", Build.VERSION.CODENAME );
		ret.put( "INCREMENTAL", Build.VERSION.INCREMENTAL );
		ret.put( "RELEASE", Build.VERSION.RELEASE );
		ret.put( "SDK_INT", String.valueOf( Build.VERSION.SDK_INT ));
		
		return ret;
	}
	
	/**
	 * 단말기의 이름을 반환합니다. <br>
	 * assets 폴더에 supported_devices_out.csv 파일이 있어야 합니다. <br>
	 * 해당 파일은 구글의 지원단말기 리스트에서 받을 수 있습니다. ( http://storage.googleapis.com/play_public/supported_devices.csv ) 
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceName( Context context ) {
		final String findString = Build.MODEL;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			isr = new InputStreamReader( context.getAssets().open( chars2string('s','u','p','p','o','r','t','e','d','_','d','e','v','i','c','e','s','_','o','u','t','.','c','s','v' )), "UTF-8");
			//isr = new InputStreamReader( context.getAssets().open( "supported_devices_out.csv" ), "UTF-8");
			br = new BufferedReader( isr ) ;
			
			String line;
			while(( line = br.readLine()) != null ) {
				if( line.indexOf( findString ) > -1 ) {
					String token[] = line.split( chars2string(','));
					//String token[] = line.split( "," );
					return String.format( chars2string( '%','s',' ','%','s' ), token[0], token[1] );
					//return String.format( "%s %s", token[0], token[1] );
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if( br != null ) {
				try { br.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if( isr != null ) {
				try { isr.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}

		return findString;
	}
	
	private static String chars2string( char ... args ) {
		int len = args.length;
		char chars[] = new char[len];
		
		int i = 0;
		for( char ch : args ) {
			chars[i++] = ch;
		}
		
		return String.valueOf( chars, 0, len );
	}
	
	/**
	 * 단말기가 비행기 모드인지를 확인해 줍니다. 
	 * 
	 * @param context
	 * @return true : 비행기 모드 
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static boolean isAirplaneMode( Context context ) {
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        } 
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
	}
	
	public static boolean isRooted() {
	    return testSuBinary() || findSuBinary() || fineSuperSuApk();
	}

	private static boolean fineSuperSuApk() {
		String places [] = {
			chars2string( '/','s','y','s','t','e','m','/','a','p','p','/','S','u','p','e','r','u','s','e','r','.','a','p','k' ), 
			chars2string( '/','d','a','t','a','/','a','p','p','/','e','u','.','c','h','a','i','n','f','i','r','e','.','s','u','p','e','r','s','u','.','a','p','k'),
			chars2string( '/','d','a','t','a','/','a','p','p','/','e','u','.','c','h','a','i','n','f','i','r','e','.','s','u','p','e','r','s','u','-','1','.','a','p','k') 
	    };
	    
	    for (String where : places) {
	    	File file = new File( where );
	    	if ( file.exists() ) {
	    		Log.d( LOG_TAG, "Found su apk file at : " + file.getAbsolutePath());
	    		return true;
	    	}
	    }
	    return false;
	}
	
	private static boolean findSuBinary() {
	    String places [] = {
	    		chars2string( '/','s','b','i','n','/','s','u'),
	    		chars2string( '/','s','y','s','t','e','m','/','b','i','n','/','s','u'),
	    		chars2string( '/','s','y','s','t','e','m','/','x','b','i','n','/','s','u'), 
	    		chars2string( '/','d','a','t','a','/','l','o','c','a','l','/','x','b','i','n','/','s','u'),
	    		chars2string( '/','d','a','t','a','/','l','o','c','a','l','/','b','i','n','/','s','u'), 
	    		chars2string( '/','s','y','s','t','e','m','/','s','d','/','x','b','i','n','/','s','u'), 
	    		chars2string( '/','s','y','s','t','e','m','/','b','i','n','/','f','a','i','l','s','a','f','e','/','s','u'), 
	    		chars2string( '/','d','a','t','a','/','l','o','c','a','l','/','s','u'),
	    		chars2string( '/','s','y','s','t','e','m','/','x','b','i','n','/','d','a','e','m','o','n','s','u'),
	    		chars2string( '/','s','u','/','b','i','n','/','s','u'),
	    		chars2string( '/','s','u','/','x','b','i','n','/','s','u'),
	    		chars2string( '/','s','u','/','b','i','n','/','d','a','e','m','o','n','s','u'),
	    };
	    
	    for (String where : places) {
	    	File file = new File( where );
	    	if ( file.exists() ) {
	    		Log.d( LOG_TAG, "Found su binary at : " + file.getAbsolutePath());
	    		return true;
	    	}
	    }
	    return false;
	}
	
	private static boolean testSuBinary() {
		boolean result = false;
		
		Process process = null;
		try {
			process = Runtime.getRuntime().exec( chars2string( 's','u' ));
			result = true;
			
		} catch (Exception e) {
		} finally {
			if( process != null ) {
				process.destroy();
			}
		}

		return result;
	}
}
