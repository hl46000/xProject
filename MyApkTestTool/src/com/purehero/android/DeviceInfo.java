package com.purehero.android;

import com.android.ddmlib.IDevice;

public class DeviceInfo extends LogCat {
	public DeviceInfo( IDevice _device ) {
		super( _device );
	}
	
	public IDevice getInterface() { return device; }
		
	public String getModelName() 	{ 
		String modelName = device.getProperty( IDevice.PROP_DEVICE_MODEL ); 
		if( modelName != null ) return modelName.toUpperCase();
		return modelName; 
	}
	public String getSerialNumber() { return device.getSerialNumber(); }
	public String getOsVersion() 	{ return device.getProperty( IDevice.PROP_BUILD_VERSION ); }
	public String getState() 		{ return device.getState().name(); }
	public Integer getBatteryLevel() {
		try { 
			return device.getBattery().get(); 
		} catch (Exception e) {}
		return 0;
	}

	private boolean selected = false;
	public Boolean getSelected() { return selected; }
	public void setSelected(Boolean selected) { this.selected = selected; }
	
	private String commant = "idle";
	public String getCommant() { return commant; }
	public void setCommant( String _commant ) { this.commant = _commant; }
	
	// 카운트
	private int count = 0;
	public int getCount() { return count; }
	public void setCount( int _count ) { this.count = _count; }
	
	// 오류 카운트
	private int errorCount = 0;
	public int getErrorCount() { return errorCount; }
	public void setErrorCount( int _errorCount ) { this.errorCount = _errorCount; }
}
