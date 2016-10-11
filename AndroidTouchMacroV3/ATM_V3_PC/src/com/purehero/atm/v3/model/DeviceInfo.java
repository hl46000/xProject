package com.purehero.atm.v3.model;

import com.android.ddmlib.IDevice;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DeviceInfo {
	public IDevice device;
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private String serialNumber = "X";
	private String model = "X";
	private String os_ver = "X";
	private String status;
	private int batteryLevel = 0;
		
	public DeviceInfo( IDevice _device, String serial, String _model, String _os_ver ) {
		device			= _device;
		serialNumber 	= serial;
		model			= _model;
		os_ver			= _os_ver;
	};
	
	public Boolean getSelected() { return selected.get(); }
	public void setSelected(Boolean selected) { this.selected.set( selected ); }
	
	public String getSerialNumber() { return serialNumber; }
	public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }
	
	public String getOs_ver() { return os_ver; }
	public void setOs_ver(String os_ver) { this.os_ver = os_ver; }

	public String getStatus() { return status; }
	public void setStatus( String status ) { this.status = status; }
	
	public int getBatteryLevel() { return batteryLevel; }
	public void setBatteryLevel( int batteryLevel ) { this.batteryLevel = batteryLevel; }
	
	public void print() {
		System.out.println( "[AdbDevice] ================================" );
		System.out.println( "[AdbDevice] serialNumber : " + serialNumber );
		System.out.println( "[AdbDevice] model : " + model );
		System.out.println( "[AdbDevice] os_ver : Android " + os_ver );
		System.out.println( "[AdbDevice] status : " + status );
		System.out.println( "[AdbDevice] battery level : " + batteryLevel );
		System.out.println( "[AdbDevice] ================================" );
	}
}
