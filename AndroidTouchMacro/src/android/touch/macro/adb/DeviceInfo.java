package android.touch.macro.adb;

import android.touch.macro.util.Log;

public class DeviceInfo {
	public DeviceInfo( String serialNumber, String product, String model, String device, String os_ver ) {
		this.serialNumber 	= serialNumber;
		this.product		= product;
		this.model			= model;
		this.device			= device;
		this.os_ver			= os_ver;
	}
	public void print() {
		Log.i( "%s\tdevice product:%s model:%s device:%s", serialNumber, product, model, device );
	}
	public String serialNumber;
	public String product;
	public String model;
	public String device;
	public String os_ver;
	public String wipi_ip;
};
