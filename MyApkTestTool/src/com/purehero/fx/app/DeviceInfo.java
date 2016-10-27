package com.purehero.fx.app;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatReceiverTask;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DeviceInfo {
	private final IDevice device;
	private LogCatReceiverTask logcatTask;
	
	public DeviceInfo( IDevice _device ) {
		device = _device;
	}
	
	private boolean bIsLogcatStarted = false;
	public boolean isLogcatStarted() { return bIsLogcatStarted; }
	public void startLogCat( LogCatListener listener ) {
		logcatTask = new LogCatReceiverTask(device);
		logcatTask.removeLogCatListener( listener );
		logcatTask.addLogCatListener( listener );
		
		new Thread( new Runnable(){
			@Override
			public void run() {
				bIsLogcatStarted = true;
				logcatTask.run();
				bIsLogcatStarted = false;
			}}).start();
	}
	public void stopLogCat() {
		logcatTask.stop();
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
