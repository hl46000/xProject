package android.touch.macro.v2.adb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import android.touch.macro.util.Util;
import android.touch.macro.v2.TouchMacroV2;

public class AdbV2 {
	public static boolean debugLog = false;
	private static String path = null;
	
	/**
	 * @param _path
	 * @return
	 */
	public static boolean setAdbPath( String _path ) {
		if( !new File( _path ).exists()) {
			System.err.println( String.format( "[Error]\tADB_PATH : '%s' not exist", _path ));
			return false;
		}
		path = _path;		
		return true;
	}
	
	/**
	 * @param cmd
	 * @return
	 */
	public static ArrayList<String> Command( String cmd ) {
		String prog = String.format( "%s %s", path, cmd );
		if( debugLog ) {
			System.out.println( "[AdbV2] Execute : " + prog );
		}
		return Util.getRuntimeExecResult( prog );
	}
	
	/**
	 * @param cmd
	 * @param device
	 * @return
	 */
	public static ArrayList<String> Command( String cmd, AdbDevice device ) {
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
		ArrayList<String> result = Command( prog, device );
		
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
		
		ArrayList<String> result = Command("devices");
		for( String line : result ) {
			line = line.replace("\r", "").replace( "\n", "").replace("\t"," ");
			line = line.trim();
			
			if( line.length() < 5 ) continue;
			if( line.startsWith("List")) continue;
			
			String [] tokens = line.split( " " );
			
			AdbDevice device = new AdbDevice( tokens[0], null, null );
			device.setModel( getPropCommand( "ro.product.model", device ));
			device.setOs_ver( getPropCommand( "ro.build.version.release", device ));
			device.setStatus( tokens[1].compareToIgnoreCase("device") == 0 ? "connected" : tokens[1] );
			
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
	public static BufferedImage screenCapture( AdbDevice device ) {
		File localTmpFile 	= null;
		BufferedImage ret 	= null;
		
		String path = TouchMacroV2.instance.getCurrentPath();
		
		localTmpFile = new File( path, "screencap.png" );
		localTmpFile.delete();
		
		Command( "shell screencap -p /sdcard/screencap.png", device );
		Command( "pull /sdcard/screencap.png " + localTmpFile.getAbsolutePath(), device );
		
		if( localTmpFile.exists() ) {
			try {
				ret = ImageIO.read( localTmpFile );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
