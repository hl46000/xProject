package com.purehero.fx.app;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

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

	private BooleanProperty selected = new SimpleBooleanProperty(false);
	public Boolean getSelected() { return selected.get(); }
	public void setSelected(Boolean selected) { this.selected.set( selected ); }
	
	private SimpleStringProperty commant = new SimpleStringProperty("idle");
	public String getCommant() { return commant.get(); }
	public void setCommant( String _commant ) { this.commant.set( _commant ); }
	
	// 카운트
	private SimpleIntegerProperty count = new SimpleIntegerProperty(0);
	public int getCount() { return count.get(); }
	public void setCount( int _count ) { this.count.set( _count ); }
	
	// 오류 카운트
	private SimpleIntegerProperty errorCount = new SimpleIntegerProperty(0);
	public int getErrorCount() { return errorCount.get(); }
	public void setErrorCount( int _errorCount ) { this.errorCount.set( _errorCount ); }
}
