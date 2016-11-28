package com.example.apk.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

public class AndroidSensorInfo {
	
	/**
	 * @param context
	 * @return
	 */
	public static List<Map<String,String>> getInfo( Context context ) {
		SensorManager sm = (SensorManager) context.getSystemService( Context.SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
		
		List<Map<String,String>> sInfo = new ArrayList<Map<String,String>>();
		
		for (Sensor sensor : sensors) {
			Map<String,String> info = new HashMap<String,String>();
			info.put( "Name", sensor.getName());
			info.put( "Type", getSensorType( sensor.getType(), sensor.getName()));
			info.put( "Version", String.valueOf( sensor.getVersion()));
			info.put( "Vendor", sensor.getVendor());
			info.put( "Resolution", String.valueOf( sensor.getResolution()));
			info.put( "MaximumRange", String.valueOf( sensor.getMaximumRange()));
			info.put( "Power", String.valueOf( sensor.getPower()));
			
			sInfo.add( info );
        }
		return sInfo;
	}
	
	@SuppressWarnings("deprecation")
	private static String getSensorType( int sensorType, String sensorName )
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
        
        return String.format( Locale.getDefault(), "Unknown Type(%d)", sensorType );
    }
}
