package android.touch.macro.v2.adb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import android.touch.macro.v2.CallbackMessage;
import android.touch.macro.v2.UtilV2;

public class AdbV2 {
	public static boolean debugLog = false;
	private static String adb_path = null;
	private static String aapt_path = null;
		
	public static boolean setAaptPath( String _path ) {
		if( !new File( _path ).exists()) {
			System.err.println( String.format( "[Error]\tAAPT_PATH : '%s' not exist", _path ));
			return false;
		}
		aapt_path = _path;		
		return true;		
	}
	
	/**
	 * @param _path
	 * @return
	 */
	public static boolean setAdbPath( String _path ) {
		if( !new File( _path ).exists()) {
			System.err.println( String.format( "[Error]\tADB_PATH : '%s' not exist", _path ));
			return false;
		}
		adb_path = _path;		
		return true;
	}
	
	/**
	 * @param cmd
	 * @return
	 */
	public static void Command( String cmd, CallbackMessage callback ) {
		String prog = String.format( "%s %s", adb_path, cmd );
		if( debugLog ) {
			System.out.println( "[AdbV2] Execute : " + prog );
		}
		
		UtilV2.getRuntimeExecResult( prog, callback );
	}
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static void Command( String cmd, AdbDevice device, CallbackMessage callback ) {
		String prog = null;
		if( device == null ) {
			prog = String.format( "%s", cmd );
		} else {
			prog = String.format( "-s %s %s", device.getSerialNumber(), cmd );
		}
		Command( prog, callback );
	}
	
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static void CommandThread( List<String> cmds, AdbDevice device, CallbackMessage callback ) {
		new Thread( new CommandThread( cmds, device, callback )).start();
	}
	
	static class CommandThread implements Runnable {
		AdbDevice device;
		CallbackMessage callback;
		List<String> cmds;
		
		public CommandThread( List<String> _cmds, AdbDevice _device, CallbackMessage _callback ) {
			cmds 		= _cmds;
			device		= _device;
			callback 	= _callback;
		}

		@Override
		public void run() {
			String prog = null;
			
			for( String cmd : cmds ) {
				if( device == null ) {
					prog = String.format( "%s", cmd );
				} else {
					prog = String.format( "-s %s %s", device.getSerialNumber(), cmd );
				}
				Command( prog, callback );
			}
		}
	}
	
	
	/**
	 * @param cmd
	 * @return
	 */
	public static List<String> Command( String cmd ) {
		String prog = String.format( "%s %s", adb_path, cmd );
		if( debugLog ) {
			System.out.println( "[AdbV2] Execute : " + prog );
		}
		
		return UtilV2.getRuntimeExecResult( prog, null );
	}
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static List<String> Command( String cmd, AdbDevice device ) {
		String prog = null;
		if( device == null ) {
			prog = String.format( "%s", cmd );
		} else {
			prog = String.format( "-s %s %s", device.getSerialNumber(), cmd );
		}
		return Command( prog );
	}
	
	/**
	 * @param key
	 * @param device
	 * @return
	 */
	public static String getPropCommand( String key, AdbDevice device ) {
		String prog = String.format( "shell getprop %s", key );
		List<String> result = Command( prog, device );
		
		if( result.size() > 0 ) return result.get(0).trim();
		return null;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public static ArrayList<AdbDevice> getDevices() {
		ArrayList<AdbDevice> ret = new ArrayList<AdbDevice>();
		
		List<String> result = Command("devices");
		for( String line : result ) {
			line = line.replace("\r", "").replace( "\n", "").replace("\t"," ");
			line = line.trim();
			
			if( line.length() < 5 ) continue;
			if( line.startsWith("*")) continue;
			if( line.startsWith("List")) continue;
			
			String [] tokens = line.split( " " );
			
			AdbDevice device = new AdbDevice( tokens[0], "X", "X" );
			if( tokens[1].compareToIgnoreCase("device") == 0 ) {
				device.setModel( getPropCommand( "ro.product.model", device ));
				device.setOs_ver( getPropCommand( "ro.build.version.release", device ));
				device.setStatus( "Online" );
				getBatteryLevel( device );
				getDeviceOrientation( device );
				
			} else {
				device.setStatus( tokens[1] );
			}
						
			ret.add( device );
			if( debugLog ) {
				device.print();
			}
		}
		
		return ret;
	}
	
	/**
	 * 단말기의 현재 화면을 캡쳐하여 BufferedImage 객체로 반환한다.  
	 * 
	 * @param deviceInfo 여러대의 단말이 연결되어 있다면, 하나의 단말을 선택할때 사용된다. 해당값은 getDevices() method 로 없을 수 있다. 연뎔괸 단말기가 하나 일때는 null 값을 넣으면 된다.
	 * @return 현재 단말기 화면의 Image 파일의 경로, 실패 및 오류 발생 시 null 리턴
	 */
	public static BufferedImage screenCapture( AdbDevice device, File capture_file ) {
		BufferedImage ret 	= null;
		
		Command( "shell screencap -p /sdcard/screencap.png", device );
		Command( "pull /sdcard/screencap.png " + capture_file.getAbsolutePath(), device );
		
		if( capture_file.exists() ) {
			try {				
				ret = ImageIO.read( capture_file );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
		
	/**
	 * device 의 x, y 좌표를 클릭 합니다. 
	 * 
	 * @param x
	 * @param y
	 * @param device
	 */
	public static void touchScreen( int x, int y, AdbDevice device ) {
		Command( String.format( "shell input tap %d %d", x, y ), device );
	}
	
	
	/**
	 * device 의 화면을 x,y 에서 x2,y2로 swipe 합니다. swipe 시간은 swipeTime(ms) 입니다. 
	 * 
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @param swipeTime ms
	 * @param device
	 */
	public static void swipeScreen(int x, int y, int x2, int y2, long swipeTime, AdbDevice device) {
		Command( String.format( "shell input swipe %d %d %d %d %d", x, y, x2, y2, swipeTime ), device );
	}	 
	
	
	
	/**
	 * 연결된 장치의 남은 베터리의 level 값을 반환 합니다. 오류 시 -1 반환
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public static int getBatteryLevel( AdbDevice device ) {
		List<String> result = Command( "shell dumpsys battery", device );
		
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
			device.setBatteryLevel( Integer.valueOf( level ));
		}
						
		return device.getBatteryLevel();
	}
	
	/**
	 * 현재 단말기 화면의 회전 방향을 반환합니다. 
	 * 
	 * @param device
	 * @return
	 */
	public static int getDeviceOrientation( AdbDevice device ) {
		ArrayList<String> result = new ArrayList<String>(); 
		List<String> lines = Command( "shell dumpsys SurfaceFlinger", device );
		for( String line : lines ) {
			if( line.contains("orientation")) result.add( line );
		}
		
		for( String line : result ) {
			Map<String,String> itemData = new HashMap<String,String>();
			
			String items [] = line.trim().split(",");
			for( String item : items ) {
				String value [] = item.trim().split("=");
				if( value.length > 1 ) {
					itemData.put( value[0].trim().toLowerCase(), value[1].trim().toLowerCase());
				}
			}
			
			if( itemData.size() == 0 ) continue;
			try {
				String value = itemData.get("candraw");
				if( value != null ) {
					device.setDisplayOn( Integer.valueOf( value ) == 0 ? "OFF" : "ON");
				} else {
					value = itemData.get("isdisplayon");
					if( value != null ) {
						device.setDisplayOn( Integer.valueOf( value ) == 0 ? "OFF" : "ON");
					}
				}
			} catch( Exception e ) {}
				
			try {
				String value = itemData.get("orientation");
				if( value != null ) {
					device.setOrientation( Integer.valueOf( value ));
				}
			} catch( Exception e ) {}			
		}
				
		return device.getOrientation();
	}

	/**
	 * APK 파일에서 package name 을 추출하여 반환하여 줍니다. 
	 * 
	 * @param apk_file
	 * @return
	 */
	public static Map<String,String> getInformationFromApk(File apk_file, List<String> keys ) {
		Map<String,String> ret = new HashMap<String, String>();
		
		String prog = String.format( "%s dump badging %s", aapt_path, apk_file.getAbsolutePath() );
		if( debugLog ) {
			System.out.println( "[AdbV2] Execute : " + prog );
		}
		
		for( String line : UtilV2.getRuntimeExecResult( prog, null )) {
			for( String key : keys ) {
				String findString = key + ": name='";
				if( line.startsWith( findString )) {
					String strTemp = line.substring( findString.length());
					String token[] = strTemp.split("' ");
					if( token.length > 0 ) {
						ret.put(key, token[0]);						
					}
				}
			}
		}
		
		return ret;
	}
}
