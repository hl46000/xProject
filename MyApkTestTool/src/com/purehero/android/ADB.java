package com.purehero.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;

public class ADB implements IDeviceChangeListener {
	private AndroidDebugBridge bridge = null;
	private List<IDevice> devices = new ArrayList<IDevice>();
	private DeviceChangeListener changeListener = null;
	
	/**
	 * 디바이스 변경 감지 Listener 
	 * 
	 * @param changeListener
	 */
	public void setDeviceChangeListener ( DeviceChangeListener changeListener ) {
		this.changeListener = changeListener;
	}
	
	private File adb = null;
		
	/**
	 * ADB 초기화 함수, adbPath 필요
	 * 
	 * @param adbPath
	 * @return
	 */
	public boolean Initialize( File adbPath ) {
		// Get a device bridge instance. Initialize, create and restart.
		try {
			AndroidDebugBridge.initIfNeeded(false);
			
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
			System.err.println("The IllegalStateException is not a show " +
			"stopper. It has been handled. This is just debug spew." +
			" Please proceed.");
			
			return false;
		}
		
		try {
			bridge = AndroidDebugBridge.getBridge();			
		} catch( Exception e ) {}
		
		adb = adbPath;
		try {
			if (bridge == null) {		
				bridge = AndroidDebugBridge.createBridge( adbPath.getAbsolutePath(), false);
			}
		} catch( Exception e ) {}
		
		AndroidDebugBridge.addDeviceChangeListener(this);
		return true;
	}
	
	/**
	 * 리소스 반환함수, Listener 및 ADB 관련 리소스를 해제한다. 
	 */
	public void Release() {
		AndroidDebugBridge.removeDeviceChangeListener(this);
		try {
			AndroidDebugBridge.terminate();
		} catch( Exception e ) {}
		
		System.out.println( "ADB Release" );
	}

	/**
	 * ADB에 연결되어 있는 단말기들의 IDevice 객체를 반환한다. 
	 * 
	 * @return
	 */
	public List<IDevice> getDevices() {
		return devices;
	}
	
	// ADB에 새로운 장비가 연결되었을 때 호출되는 함수, 단말기 리스트에 새로 연결된 단말기를 추가한다. 
	@Override
	public void deviceConnected(IDevice device) {
		if( !devices.contains( device )) {
			devices.add( device );
		}
		if( changeListener != null ) {
			changeListener.OnDeviceChangedEvent();
		}
	}

	// ADB에 단말기의 연결이 끊겼을때 호출되는 함수, 단말기 리스트에서 끊긴 단말기를 삭제한다. 
	@Override
	public void deviceDisconnected(IDevice device) {
		devices.remove(device);
		if( changeListener != null ) {
			changeListener.OnDeviceChangedEvent();
		}
	}

	// ADB에 연결된 단말기의 상태가 변경되었을 경우 호출되는 함수
	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		if( changeListener != null && (IDevice.CHANGE_STATE & changeMask) != 0 ) {
			changeListener.OnDeviceChangedEvent();
		}
	}
	
	/**
	 * ADB 파일의 경로를 반환한다. 
	 * 
	 * @return
	 */
	public String getAdbPath() {
		if( adb != null ) {
			if( adb.exists()) {
				return adb.getAbsolutePath();
			}
		}
		return null;
	}
}
