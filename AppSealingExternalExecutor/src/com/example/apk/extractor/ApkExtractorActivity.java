package com.example.apk.extractor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class ApkExtractorActivity extends Activity {
	final String LOG_TAG = "TEST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	// 세로 모드
		
		Map<String,String> infos = getAndroidInfo();
		Set<String> keys = infos.keySet();
		for( String key : keys ) {
			Log.i( LOG_TAG, String.format( "%s = %s", key, infos.get( key )));
		}
		getSensorInfo();		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	 
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public boolean isAirplaneMode() {
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
			return Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        } 
        return Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
	}
	
	public Map<String,String> getAndroidInfo() {
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
		ret.put( "NAME", getDeviceName() );
	
		ret.put( "CODENAME", Build.VERSION.CODENAME );
		ret.put( "INCREMENTAL", Build.VERSION.INCREMENTAL );
		ret.put( "RELEASE", Build.VERSION.RELEASE );
		ret.put( "SDK_INT", String.valueOf( Build.VERSION.SDK_INT ));
		
		return ret;
	}
	
	/** Returns the consumer friendly device name */
	public String getDeviceName() {
		final String findString = Build.MODEL;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			isr = new InputStreamReader( ApkExtractorActivity.this.getAssets().open("supported_devices.csv"), "UTF-8");
			br = new BufferedReader( isr ) ;
			
			String line;
			while(( line = br.readLine()) != null ) {
				if( line.indexOf( findString ) > -1 ) {
					String token[] = line.split(",");
					return String.format( "%s %s", token[0], token[1] );
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

	public void getSensorInfo() {
		Log.i( LOG_TAG, "");
		Log.i( LOG_TAG, "");
		Log.i( LOG_TAG, "SENSOR INFOMATIONS");
		Log.i( LOG_TAG, "");
		Log.i( LOG_TAG, "");
		
		SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
		
		for (Sensor sensor : sensors) {
			Log.i( LOG_TAG, "NAME=" + sensor.getName());
			Log.i( LOG_TAG, "  Type: " + getSensorType(sensor.getType(), sensor.getName()));
			Log.i( LOG_TAG, "  Vendor: " + sensor.getVendor());
			Log.i( LOG_TAG, "  Version: " + sensor.getVersion());
			Log.i( LOG_TAG, "  Resolution: " + sensor.getResolution());
			Log.i( LOG_TAG, "  MaximumRange: " + sensor.getMaximumRange());
			Log.i( LOG_TAG, "  Power: " + sensor.getPower());
        }
	}
	
	private String getSensorType( int sensorType, String sensorName )
    {
		switch( sensorType ) {
		case Sensor.TYPE_ACCELEROMETER : 		return "ACCELEROMETER";
		case Sensor.TYPE_MAGNETIC_FIELD :		return "MAGNETIC_FIELD";		
		case Sensor.TYPE_GRAVITY : 				return "GRAVITY"; 				
		case Sensor.TYPE_GYROSCOPE : 			return "GYROSCOPE"; 			
		case Sensor.TYPE_LIGHT : 				return "LIGHT";
		case Sensor.TYPE_LINEAR_ACCELERATION :	return "LINEAR_ACCELERATION";
		case Sensor.TYPE_PRESSURE : 			return "PRESSURE";
		case Sensor.TYPE_PROXIMITY : 			return "PROXIMITY"; 
		case Sensor.TYPE_RELATIVE_HUMIDITY : 	return "RELATIVE_HUMIDITY";
		case Sensor.TYPE_ROTATION_VECTOR : 		return "ROTATION_VECTOR";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED : 	return "MAGNETIC_FIELD_UNCALIBRATED";
		case Sensor.TYPE_GAME_ROTATION_VECTOR :	return "GAME_ROTATION_VECTOR";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED :		return "GYROSCOPE_UNCALIBRATED";
		case Sensor.TYPE_SIGNIFICANT_MOTION :	return "SIGNIFICANT_MOTION";
		case Sensor.TYPE_STEP_DETECTOR :		return "STEP_DETECTOR";
		case Sensor.TYPE_STEP_COUNTER :			return "STEP_COUNTER";
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR :	return "GEOMAGNETIC_ROTATION_VECTOR";
		}
		  
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ){
        	if( sensorType == Sensor.TYPE_TEMPERATURE ) 		return "TEMPERATURE";
        } else {
        	if( sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE ) return "TEMPERATURE";
        }
        
        if( sensorType == 3 ) return "ORIENTATION";
        if( sensorName.contains("Orientation Sensor")) return "ORIENTATION";
        
        return String.format( "Unknown Type(%d)", sensorType );
    }
}
