package android.touch.macro.adb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import android.touch.macro.G;
import android.touch.macro.util.Log;
import android.touch.macro.util.Util;

/**
 * 
 * 
 * @author MY
 *
 */
public class AdbCommand {
	
	private static String getAdbPath( DeviceInfo deviceInfo ) {
		String adbPath = G.getDefaultProperties().getProperty("ADB_PATH");
		if( adbPath == null ) {
			String message = G.getDefaultPath() + "/AndroidTouchMacro.properties 파일이 있는지 확인해 주세요\n";
			message += "해당 파일에 ADB_PATH=<adb 파일의 전체 경로> 값이 정의 되어 있는지 확인해 주세요\n";
			message += "확인 후 다시 실행해 주시기 바랍니다. ";
			
			Util.alert( "Notice", message );
			System.exit(-1);
		}
		if( deviceInfo != null ) {
			adbPath = String.format( "%s -s %s", adbPath, deviceInfo.serialNumber );
		}
		return adbPath;
	}
	
	/**
	 * 연결된 장치에 Key event 값을 보낸다. 
	 * 
	 * @param deviceInfo
	 * @param keyEventValue
	 */
	public static void sendKeyEvent( DeviceInfo deviceInfo, String keyEventValue ) {
		String prog = String.format( "%s shell input keyevent %s", getAdbPath(deviceInfo), keyEventValue );
		Util.getRuntimeExecResult( prog );
	}
	
	/**
	 * 연결된 장치의 남은 베터리의 level 값을 반환 합니다. 오류 시 -1 반환
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public static int getBatteryLevel( DeviceInfo deviceInfo ) {
		String prog = String.format( "%s shell dumpsys battery", getAdbPath(deviceInfo) );
		ArrayList<String> result = Util.getRuntimeExecResult( prog );
		
		if( result.size() < 0 ) return -1;
		for( String line : result ) {
			line = line.trim();
			
			if( line.isEmpty()) continue;
			if( line.startsWith("List")) continue;
			if( line.startsWith("* daemon")) continue;
			
			while( line.contains("  ")) {
				line = line.replace( "  ", " " );
			}
			
			if( !line.startsWith("level:")) continue;
			
			String level = line.substring( "level:".length() ).trim();
			return Integer.valueOf( level );
		}
						
		return -1;
	}
	
	/**
	 * Device 와 ADB가 연결이 되어 있는지를 확인한다. 
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public static boolean checkConnectionDevice( DeviceInfo deviceInfo ) {
		String prog = String.format( "%s get-state", getAdbPath(deviceInfo) );
		ArrayList<String> result = Util.getRuntimeExecResult( prog );
		
		if( result.size() < 0 ) return false;
		if( result.get(0).trim().compareToIgnoreCase("device") == 0 ) return true;
		return false;
	}
	
	/**
	 * 현재 PC에 연결된 Device의 정보를 반환한다. 
	 * 
	 * @return 
	 */
	public static ArrayList<DeviceInfo> getDevices() {
		String prog = String.format( "%s devices -l", G.getDefaultProperties().getProperty("ADB_PATH") );
		ArrayList<String> result_lines = Util.getRuntimeExecResult( prog );
	
		ArrayList<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		
		if( result_lines == null ) return ret;
		if( result_lines.size() < 1 ) return ret;
		 
		for( String line : result_lines ) {
			line = line.trim();
			
			if( line.isEmpty()) continue;
			if( line.startsWith("List")) continue;
			if( line.startsWith("* daemon")) continue;
			
			while( line.contains("  ")) {
				line = line.replace( "  ", " " );
			}
			//line = line.replace( "\r", "" );
			//line = line.replace( "\n", "" );
			
			if( line.isEmpty() ) continue;
			String [] tokens = line.split( " " );
			if( tokens.length == 2 ) {
				String serialNumber = tokens[0];
				String device		= Util.getColonValue( tokens[1] );
				
				DeviceInfo info = new DeviceInfo( serialNumber, serialNumber, serialNumber, device, "0" );
				info.os_ver = getAndroidVersion( info );
				//info.wipi_ip = getDeviceWifiIP( info );
				ret.add( info );
				
			} else if( tokens.length >= 5 ) {
				String serialNumber = tokens[0];
				String product		= Util.getColonValue( tokens[2] );
				String model		= Util.getColonValue( tokens[3] );
				String device		= Util.getColonValue( tokens[4] );
	
				DeviceInfo info = new DeviceInfo( serialNumber, product, model, device, "0" );
				info.os_ver = getAndroidVersion( info );
				//info.wipi_ip = getDeviceWifiIP( info );
				
				ret.add( info );
			}
		}
		
		return ret;
	}
	
	/**
	 * Device에 활성화 되어 있는 IP 정보를 반환한다. 
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public static String getDeviceWifiIP( DeviceInfo deviceInfo ) {
		String wifi_interface_name = getProp( "wifi.interface", deviceInfo );
		Log.d( "wifi_interface_name:%s", wifi_interface_name );
		
		String prog = String.format( "%s shell netcfg", getAdbPath(deviceInfo) );
		ArrayList<String> result = Util.getRuntimeExecResult( prog );
		
		for( String data : result ) {
			String strLine = data.trim().toLowerCase();
			
			while( strLine.contains("  ")) {
				strLine = strLine.replace( "  ", " " );
			}
			
			String token [] = strLine.split( " " );
			
			if( token.length < 3 ) continue;
			if( token[1].compareTo("up") != 0 ) continue;	// active
			if( token[0].compareTo("lo") == 0 ) continue;	// local loop back
			if( token[0].startsWith("rmnet")) continue;		// Mobile Network ( 데이터망 )
			if( token[2].startsWith("0.")) continue;		
			if( token[0].compareTo(wifi_interface_name) != 0 ) continue;
			
			Log.d( "%s %s %s", token[0], token[1], token[2]);
			return token[2];
		}
		
		return null;
	}
	
	/**
	 * 마지막으로 screencap 한 이미지 파일 객체를 반환합니다. 
	 * 
	 * @param deviceInfo
	 * @return 실패시 null 반환
	 */
	public static File getLastScreencapFile( DeviceInfo deviceInfo ) {
		return new File( G.getTempPath().getAbsolutePath() + "/screencap.png");
	}
	
	/**
	 * 단말기의 현재 화면을 캡쳐하여 BufferedImage 객체로 반환한다.  
	 * 
	 * @param deviceInfo 여러대의 단말이 연결되어 있다면, 하나의 단말을 선택할때 사용된다. 해당값은 getDevices() method 로 없을 수 있다. 연뎔괸 단말기가 하나 일때는 null 값을 넣으면 된다.
	 * @return 현재 단말기 화면의 BufferedImage 객체, 실패 및 오류 발생 시 null 리턴
	 */
	public static BufferedImage screencap( DeviceInfo deviceInfo ) {
		File localTmpFile 	= null;
		BufferedImage ret 	= null;
		
		try {
			localTmpFile = File.createTempFile( "tmp", "img", G.getTempPath() );
			localTmpFile.delete();
			localTmpFile = new File( localTmpFile.getParent() + "/screencap.png");

			String adbCmd = getAdbPath(deviceInfo);
						
			Util.RuntimeExec( String.format( "%s shell screencap -p /sdcard/screencap.png", adbCmd ));
			Util.RuntimeExec( String.format( "%s pull /sdcard/screencap.png %s", adbCmd, localTmpFile.getAbsolutePath()));
			Util.RuntimeExec( String.format( "%s shell rm /sdcard/screencap.png", adbCmd ));
			//Util.RuntimeExec( String.format( "%s shell screencap -p | sed 's/\r$//' > %s", adbCmd, localTmpFile.getAbsolutePath()));
			
			
			if( localTmpFile.exists() ) {
				ret = ImageIO.read( localTmpFile );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		/* 임시로 사용된 파일은 삭제한다. */
		/*
		if( localTmpFile != null ) {
			localTmpFile.delete();
		}
		*/
		
		return ret;
	}
	
	/**
	 * 단말기의 Property 값을 읽어 온다. 
	 * 
	 * @param key 읽고 싶은 Property 의 key 값
	 * @param deviceInfo 여러대의 단말이 연결되어 있다면, 하나의 단말을 선택할때 사용된다. 해당값은 getDevices() method 로 없을 수 있다. 연뎔괸 단말기가 하나 일때는 null 값을 넣으면 된다. 
	 * @return key 값에 해당하는 Property 의 value 값, key 가 없거나 오류 발생 시 null 리턴
	 */
	public static String getProp( String key, DeviceInfo deviceInfo ) {
		String prog = String.format( "%s shell getprop %s", getAdbPath(deviceInfo), key );
		ArrayList<String> result = Util.getRuntimeExecResult( prog );
		
		if( result.size() > 0 ) return result.get(0).trim();
		return null;
	}
	
	/**
	 * 단말기의 Android OS Version 정보를 읽어 온다. 
	 * 
	 * @param deviceInfo
	 * @return 
	 */
	public static String getAndroidVersion( DeviceInfo deviceInfo ) {
		return getProp( "ro.build.version.release", deviceInfo );
	}
	
	
	/**
	 * 현재 단말기의 화면 Orientation 정보를 반환 한다. 
	 * @param deviceInfo
	 * @return null(ERROR)<br><br>
	 * 0 (default, portrait),<br>
	 * 1 (device 90 degree counterclockwise),<br>
	 * 3 (device 90 degree clockwise)
	 */
	public static String getOrientation( DeviceInfo deviceInfo ) {
		/*
		String adbCmd = "adb";
		if( deviceInfo != null ) {
			adbCmd = String.format( "adb -s %s", deviceInfo.serialNumber );
		}
		
		String prog = String.format( "%s shell dumpsys display", adbCmd );
		ArrayList<String> lines = Util.getRuntimeExecResult( prog );
		
		for( String line : lines ) {
			if( !line.contains( "orientation=")) continue;
			
			String [] token = line.split( "orientation=" );
			if( token == null ) continue;
			if( token.length < 2 ) continue;
			
			return token[1].substring(0,1);
		}
		
		return null;
		*/
		return "1";
	}
	
	/**
	 * 단말기의 Touchscreen 장치 명을 획득하다. 
	 * 
	 * @param deviceInfo 여러대의 단말이 연결되어 있다면, 하나의 단말을 선택할때 사용된다. 해당값은 getDevices() method 로 없을 수 있다. 연뎔괸 단말기가 하나 일때는 null 값을 넣으면 된다. 
	 * @return touchscreen 의 장치 명, 실패 시에 null 값이 리턴된다.
	 */
	public static String getTouchDeviceName( DeviceInfo deviceInfo ) {
		String prog = String.format( "%s shell getevent -p", getAdbPath(deviceInfo) );
		ArrayList<String> result = Util.getRuntimeExecResult( prog );
		
		String findDeviceName = null;
		boolean found = false;
		
		for( String data : result ) {
			String strLine = data.trim().toLowerCase();
			if( strLine.startsWith( "add device" )) {
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					findDeviceName = token[1].trim();
				}
			} else if( strLine.startsWith( "name:" )) {
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					if( token[1].contains( "touchscreen" ) || 
						token[1].contains( "touch_dev" )   ||
						token[1].contains( "genymotion virtual input" )   ||						
						token[1].contains( "bluestacks virtual mouse" )) {
						found = true;
						break;
					}
				}
			}
		}
		
		return found ? findDeviceName : null;
	}

	/**
	 * PC의 ADB Server를 재 시작 합니다. 
	 */
	public static void restartServer() {
		String adbPath = G.getDefaultProperties().getProperty("ADB_PATH");
		String prog = String.format( "%s kill-server", adbPath );
		Util.getRuntimeExecResult( prog );
		
		prog = String.format( "%s start-server", adbPath );
		Util.getRuntimeExecResult( prog );
	}
}

