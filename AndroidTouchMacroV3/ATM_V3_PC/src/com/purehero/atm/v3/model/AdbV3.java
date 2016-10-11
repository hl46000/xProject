package com.purehero.atm.v3.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

public class AdbV3 {
	public static boolean debugLog = false;
	private static String adb_path = null;
	private static String aapt_path = null;
	
	private static AndroidDebugBridge mAndroidDebugBridge;
	
	/**
	 * @param _path
	 * @return
	 */
	public static boolean setAdbPath( String _path ) {
		if( !new File( _path ).exists()) {
			System.err.println( String.format( "[Error]\tADB_PATH : '%s' not exist", _path ));
			return false;
		}
		
		mAndroidDebugBridge = AndroidDebugBridge.createBridge( _path, true);
		
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
	 * @param device
	 * @return
	 */
	public static void Command( String cmd, DeviceInfo device_info, CallbackMessage callback ) throws Exception {
		device_info.device.executeShellCommand( cmd, new IShellOutputReceiver(){

			@Override
			public void addOutput(byte[] data, int offset, int length) {
				if( callback != null ) {
					callback.callbackMessage(new String( data, offset, length ));
				}
			}

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}});
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	public static ArrayList<DeviceInfo> getDevices() throws Exception {
		if( mAndroidDebugBridge == null ) return null;
		
		boolean bInitial = mAndroidDebugBridge.hasInitialDeviceList();
		for( int i = 0; !bInitial && i < 100; i++ ) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			bInitial = mAndroidDebugBridge.hasInitialDeviceList();
		}
		if( !bInitial ) return null;
		
		IDevice[] devices = mAndroidDebugBridge.getDevices();
				
		ArrayList<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		for( IDevice device : devices ) {
			String serial_number	= device.getSerialNumber();
			String model_name  		= device.getProperty("ro.product.model");
			String os_version		= device.getProperty("ro.build.version.release");
			
			DeviceInfo device_info = new DeviceInfo( device, serial_number, model_name, os_version );
			device_info.setBatteryLevel( device.getBatteryLevel().intValue() );
			device_info.setStatus( device.getState().name() );
			
			// getDeviceOrientation( device_info, device );
			
			ret.add( device_info );
		}
		
		
		return ret;
	}
	
	/**
	 * 단말기의 현재 화면을 캡쳐하여 BufferedImage 객체로 반환한다.  
	 * 
	 * @param deviceInfo 여러대의 단말이 연결되어 있다면, 하나의 단말을 선택할때 사용된다. 해당값은 getDevices() method 로 없을 수 있다. 연뎔괸 단말기가 하나 일때는 null 값을 넣으면 된다.
	 * @return 현재 단말기 화면의 Image 파일의 경로, 실패 및 오류 발생 시 null 리턴
	 */
	public static BufferedImage screenCaptureEx( DeviceInfo device ) throws Exception {
		RawImage rawImage = device.device.getScreenshot();
		BufferedImage ret = new BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_ARGB);
		
		int index = 0;
	    int IndexInc = rawImage.bpp >> 3;
	    for (int y = 0 ; y < rawImage.height ; y++) {
	        for (int x = 0 ; x < rawImage.width ; x++) {
	            int value = rawImage.getARGB(index);
	            index += IndexInc;
	            ret.setRGB(x, y, value);
	        }
	    }
	    
		return ret;
	}
	
	/**
	 * ?��결된 ?��치의 ?��?? 베터리의 level 값을 반환 ?��?��?��. ?���? ?�� -1 반환
	 * 
	 * @param deviceInfo
	 * @return
	 * @throws ShellCommandUnresponsiveException 
	 * @throws IOException 
	 * @throws AdbCommandRejectedException 
	 * @throws TimeoutException 
	 */
	public static int getBatteryLevel( DeviceInfo device_info ) throws Exception {
		device_info.setBatteryLevel( device_info.device.getBatteryLevel().intValue());
		return device_info.getBatteryLevel();
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
