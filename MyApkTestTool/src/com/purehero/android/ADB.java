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
	
	public void setDeviceChangeListener ( DeviceChangeListener changeListener ) {
		this.changeListener = changeListener;
	}
	
	private File adb = null;
		
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
	 * 
	 */
	public void Release() {
		AndroidDebugBridge.removeDeviceChangeListener(this);
		try {
			AndroidDebugBridge.terminate();
		} catch( Exception e ) {}
		
		System.out.println( "ADB Release" );
	}

	public List<IDevice> getDevices() {
		return devices;
	}
	
	@Override
	public void deviceConnected(IDevice device) {
		if( !devices.contains( device )) {
			devices.add( device );
		}
		if( changeListener != null ) {
			changeListener.OnDeviceChangedEvent();
		}
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		devices.remove(device);
		if( changeListener != null ) {
			changeListener.OnDeviceChangedEvent();
		}
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		if( changeListener != null && (IDevice.CHANGE_STATE & changeMask) != 0 ) {
			changeListener.OnDeviceChangedEvent();
		}
	}
	
	/**
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
