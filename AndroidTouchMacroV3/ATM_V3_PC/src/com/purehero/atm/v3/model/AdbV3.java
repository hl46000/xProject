package com.purehero.atm.v3.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class AdbV3 {
	public static boolean debugLog = false;
	private static String adb_path = null;
	private static String aapt_path = null;
		
	
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
	
	public static boolean setAaptPath( String _path ) {
		if( !new File( _path ).exists()) {
			System.err.println( String.format( "[Error]\tAAPT_PATH : '%s' not exist", _path ));
			return false;
		}
		aapt_path = _path;		
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
		
		UtilV3.getRuntimeExecResult( prog, callback );
	}
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static void Command( String cmd, DeviceInfo device, CallbackMessage callback ) {
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
	public static void CommandThread( List<String> cmds, DeviceInfo device, CallbackMessage callback ) {
		new Thread( new CommandThread( cmds, device, callback )).start();
	}
	
	static class CommandThread implements Runnable {
		DeviceInfo device;
		CallbackMessage callback;
		List<String> cmds;
		
		public CommandThread( List<String> _cmds, DeviceInfo _device, CallbackMessage _callback ) {
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
		
		return UtilV3.getRuntimeExecResult( prog, null );
	}
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static List<String> Command( String cmd, DeviceInfo device ) {
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
	public static String getPropCommand( String key, DeviceInfo device ) {
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
	public static ArrayList<DeviceInfo> getDevices() {
		ArrayList<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		
		List<String> result = Command("devices");
		for( String line : result ) {
			line = line.replace("\r", "").replace( "\n", "").replace("\t"," ");
			line = line.trim();
			
			if( line.length() < 5 ) continue;
			if( line.startsWith("*")) continue;
			if( line.startsWith("List")) continue;
			
			String [] tokens = line.split( " " );
			
			DeviceInfo device = new DeviceInfo( tokens[0], "X", "X" );
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
	public static BufferedImage screenCapture( DeviceInfo device, File capture_file ) {
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
	
	@SuppressWarnings("resource")
	public static BufferedImage screenCaptureEx( DeviceInfo device ) {
		BufferedImage ret 	= null;
		String cmd = "shell screencap -p | sed 's/\\r$//'";
		String prog = null;
		if( device == null ) {
			prog = String.format( "%s %s", adb_path, cmd );
		} else {
			prog = String.format( "%s -s %s %s", adb_path, device.getSerialNumber(), cmd );
		}
		
		InputStream input = null;
		InputStream error = null;
		Process process = null;
		
		try {
			process = Runtime.getRuntime().exec( prog );
			
			input = process.getInputStream();
			error = process.getErrorStream();
		
			int buffer_size = 1024 * 1024;
			
			FileOutputStream fos = new FileOutputStream("d:\\a.png"); 
			byte [] buffer = new byte[ buffer_size ];
			
			int nRead = 0;
			while(( nRead = input.read( buffer, 0, buffer_size)) > 0 ) {
				fos.write( buffer, 0, nRead );
			}
			fos.close();
			//ret = ImageIO.read( input );
			
			Scanner error_scaner = new Scanner(error).useDelimiter("\\n");
			while( error_scaner.hasNext() ) {
				System.err.println( error_scaner.next() );
			}
		} catch (IOException e1 ) {
			e1.printStackTrace();
			
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if( error != null ) {
				try {
					error.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * device ?�� x, y 좌표�? ?���? ?��?��?��. 
	 * 
	 * @param x
	 * @param y
	 * @param device
	 */
	public static void touchScreen( int x, int y, DeviceInfo device ) {
		Command( String.format( "shell input tap %d %d", x, y ), device, null );
	}
	
	
	/**
	 * device ?�� ?��면을 x,y ?��?�� x2,y2�? swipe ?��?��?��. swipe ?��간�? swipeTime(ms) ?��?��?��. 
	 * 
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @param swipeTime ms
	 * @param device
	 */
	public static void swipeScreen(int x, int y, int x2, int y2, long swipeTime, DeviceInfo device) {
		Command( String.format( "shell input swipe %d %d %d %d %d", x, y, x2, y2, swipeTime ), device );
	}	 
	
	
	
	/**
	 * ?��결된 ?��치의 ?��?? 베터리의 level 값을 반환 ?��?��?��. ?���? ?�� -1 반환
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public static int getBatteryLevel( DeviceInfo device ) {
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
			
			System.out.println( "Battery level : " + level );
		}
						
		return device.getBatteryLevel();
	}
	
	/**
	 * ?��?�� ?��말기 ?��면의 ?��?�� 방향?�� 반환?��?��?��. 
	 * 
	 * @param device
	 * @return
	 */
	public static int getDeviceOrientation( DeviceInfo device ) {
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
	 * ADB Shell 창을 띄워 줍니다. 
	 * 
	 * @param deviceInfo
	 */
	public static void OpenShell(DeviceInfo deviceInfo) {
		new Thread( new Runnable(){
			@Override
			public void run() {
				String cmd = "cmd.exe /c start";
				
				String prog = null;
				if( deviceInfo == null ) {
					prog = String.format( "%s %s shell", cmd, adb_path );
				} else {
					prog = String.format( "%s %s -s %s shell", cmd, adb_path, deviceInfo.getSerialNumber() );
				}
				
				if( debugLog ) {
					System.out.println( "[AdbV3] Execute : " + prog );
				}
				
				UtilV3.getRuntimeExecResult( prog, null );
			}}).start();
	}
}
