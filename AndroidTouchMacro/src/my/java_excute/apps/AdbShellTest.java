package my.java_excute.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.AdbShell;
import android.touch.macro.adb.AdbShellCallback;
import android.touch.macro.adb.DeviceInfo;

public class AdbShellTest {
	//final int TESTTING_TIME = 1000 * 60 * 60; // 1시간
	static int TESTTING_TIME = 1000 * 60; // 1분
	
	static String DATA_SAVE_PATH = "c:\\workTemp\\test";
	static String GREP_STRING = "pizza";
	
	public static void main(String[] args) {
		if( args.length < 3 ) {
			System.out.println( "USAGE : java -jar <this jar file> <SAVE_PATH> <GREP_STRING> <TEST TIME : milliseconds>" );
			return;
		}
		
		DATA_SAVE_PATH 	= args[0];
		GREP_STRING 	= args[1];
		TESTTING_TIME 	= Integer.valueOf( args[2] );
		
		Vector<AdbThrad> VtThread = new Vector<AdbThrad>();
		
		ArrayList<DeviceInfo> deviceInfos = AdbCommand.getDevices();
		for( DeviceInfo info : deviceInfos ) {
			AdbThrad at = new AdbThrad( info );
			VtThread.add( at );
			
			at.start();
		}
		
		for( AdbThrad adb : VtThread ) {
			try {
				adb.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class AdbThrad extends Thread implements AdbShellCallback{
		DeviceInfo info;
		FileOutputStream fos = null;
		
		public AdbThrad( DeviceInfo _info ) {
			info = _info;
		}
		
		@Override
		public void run() {
			File file = new File( String.format( "%s\\%s", DATA_SAVE_PATH, info.model ));
			file.getParentFile().mkdirs();
			
			try {
				fos = new FileOutputStream( file );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			AdbShell shell = new AdbShell();
			if( shell.connect( info )) {
				shell.command("top", this );
				
				try {
					Thread.sleep( TESTTING_TIME );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				shell.close();
			}
			
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fos = null;
		}

		long last_time = 0;
		int count = 0;
		
		@Override
		public void callback(AdbShell shell, String data) {
			// grep 명령어가 실행되지 못하는 단말기도 있다. 
			if( !data.contains( GREP_STRING )) return;
			
			long current_time = System.currentTimeMillis();
			
			if( fos != null ) {
				try {
					if( current_time - last_time > 1000 ) {
						fos.write( String.format( "[ %d ]\r\n", ++count ).getBytes());
					}
					fos.write(data.getBytes());
					fos.write("\r\n".getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			last_time = current_time;
		}
	}
}
