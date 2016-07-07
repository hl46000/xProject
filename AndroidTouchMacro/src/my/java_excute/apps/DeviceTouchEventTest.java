package my.java_excute.apps;

import java.util.ArrayList;

import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.AdbShell;
import android.touch.macro.adb.AdbShellCallback;
import android.touch.macro.adb.DeviceInfo;
import android.touch.macro.recoder.TouchEventValue;

public class DeviceTouchEventTest {
	public DeviceTouchEventTest(String[] args) {
		ArrayList<DeviceInfo> deviceInfos = AdbCommand.getDevices();
		for( DeviceInfo deviceInfo : deviceInfos ) {
			if( deviceInfo.model.compareTo("SM_C105L") != 0 ) continue;
			
			final String touch_device_name = AdbCommand.getTouchDeviceName(deviceInfo);
			System.out.println( deviceInfo.model + "  " + deviceInfo.product + "  " + touch_device_name );
			
			final AdbShell adb_shell = new AdbShell();
			if( adb_shell.connect( deviceInfo )) {
				adb_shell.command( "getevent", new AdbShellCallback(){
					int count = 0;
					
					@Override
					public void callback(AdbShell shell, String line) {
						if( !line.startsWith( touch_device_name )) return;
						while( line.contains("  ")) {
							line = line.replace( "  ", " " );
						}
						
						TouchEventValue teValue = new TouchEventValue();
						teValue.parser( line );
						
						System.out.println( String.format( "%3d %04x %04x %08x(%d)", count++, teValue.type, teValue.id, teValue.value, teValue.value ));
						if( teValue.type == teValue.id && teValue.id == teValue.value && teValue.value == 0 ) {
							System.out.println();
						}
						
						if( count > 100 ) {
							adb_shell.close();
						}
					}});
			}
		}		
	}
		
	public static void main(String[] args) {
		new DeviceTouchEventTest( args );
	}
}
