package com.purehero.app;

import java.util.ArrayList;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;

public class ADB implements IDeviceChangeListener {
	private AndroidDebugBridge bridge = null;
	private ArrayList<IDevice> devices = new ArrayList<IDevice>();
	
	public boolean Initialize( String adbPath ) {
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
		
		bridge = AndroidDebugBridge.getBridge();
		if (bridge == null) {		
			bridge = AndroidDebugBridge.createBridge( adbPath, false);
		}
		
		AndroidDebugBridge.addDeviceChangeListener(this);
		return true;
	}
	
	/**
	 * 
	 */
	public void Release() {
		AndroidDebugBridge.removeDeviceChangeListener(this);
		AndroidDebugBridge.terminate();
	}

	@Override
	public void deviceConnected(IDevice device) {
		devices.add( device );
		System.out.println( "added device : " + device.getSerialNumber());
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		devices.remove( device );
		System.out.println( "removed device : " + device.getSerialNumber());
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {}
}
