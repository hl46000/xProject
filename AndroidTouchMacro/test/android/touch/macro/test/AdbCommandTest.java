package android.touch.macro.test;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.DeviceInfo;
import android.touch.macro.util.Log;


public class AdbCommandTest {

	
	@Test
	public void sendKeyEventTest() {
		AdbCommand.sendKeyEvent( null, "KEYCODE_POWER" );
	}
	
	/**
	 * 
	 */
	@Test
	public void getBatteryLevelTest() {
		int level = AdbCommand.getBatteryLevel( null );
		Log.d( "battery level : %d", level );
		//Assert.assertNotNull( );
	}
		
	/**
	 * 단말기에 활성화 되어 있는 IP 주소목록을 얻어 온다. 
	 */
	@Test
	public void getDeviceIPsTest() {
		Assert.assertNotNull( AdbCommand.getDeviceWifiIP( null ));
	}
	
	/**
	 * adb shell screencap -p /sdcard/screencap.png ��ɾ�� Image ��ü�� ���� �� �� �־�� �Ѵ�. 
	 */
	@Test
	public void screencap_test() {
		Assert.assertNotNull( AdbCommand.screencap( null ) );
	}
	
	/**
	 * 
	 */
	@Test
	public void getAndroidVersion_test() {
		String ver = AdbCommand.getAndroidVersion( null );
		Assert.assertNotNull( ver );
		
		Log.i( "Android OS Version : %s", ver );
	}
	
	
	/**
	 * adb shell getprop %s ��ɾ�� �ܸ����� property ���� �о� �ɴϴ�. 
	 */
	@Test
	public void getProp_test() {
		String key = "ro.runtime.firstboot";
		String value = AdbCommand.getProp( key, null );
		Assert.assertNotNull( value );
		
		Log.i( "%s = %s", key, value );
	}

	
	/**
	 * adb shell getevent -p ��ɾ�� �ܸ����� touchscreen �̺�Ʈ ��ġ���� ȹ���մϴ�. 
	 */
	@Test
	public void getTouchDeviceName_test() {
		String value = AdbCommand.getTouchDeviceName( null );
		Assert.assertNotNull( value );
		
		Log.i( "TouchScreen Device name = '%s'", value );
	}
	
	/**
	 * adb devices -l  
	 */
	@Test
	public void getDevices_test() {
		ArrayList<DeviceInfo> value = AdbCommand.getDevices();
		Assert.assertNotNull( value );
		
		for( DeviceInfo devInfo : value ) {
			devInfo.print();
		}
	}

	
	/**
	 * 
	 */
	@Test
	public void getOrientation_test() {
		String orientation = AdbCommand.getOrientation( null );
		Assert.assertNotNull( orientation );
		
		Log.i( "Device orientation value = '%s'", orientation );
	}
	
}
