package com.purehero.app;

import com.android.ddmlib.IDevice;

public class DeviceInfo {
	final IDevice device;
	
	public DeviceInfo( IDevice _device ) {
		device = _device;
	}
	
	public String getModelName() 	{ return device.getProperty( IDevice.PROP_DEVICE_MODEL ); }
	public String getSerialNumber() { return device.getSerialNumber(); }
	public String getOsVersion() 	{ return device.getProperty( IDevice.PROP_BUILD_VERSION ); }
	public String getState() 		{ return device.getState().name(); }
	public Integer getBatteryLevel() {
		try { 
			return device.getBattery().get(); 
		} catch (Exception e) {}
		return 0;
	}
	
}
