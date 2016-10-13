package com.purehero.atm.v3.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DeviceInfo {
	private BooleanProperty selected = new SimpleBooleanProperty(false);
	private String serialNumber = "X";
	private String model = "X";
	private String os_ver = "X";
	private String status;
	private int batteryLevel = 0;
	private int orientation;		// ?��말기?�� 방향
	private String displayOn = "X";			// ?���? 켜짐( ON:커짐, OFF:켜짐 )
	
	public DeviceInfo( String serial, String _model, String _os_ver ) {
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
	
	public int getOrientation() { return orientation; }
	public void setOrientation( int orientation ) { this.orientation = orientation; }
	
	public int getBatteryLevel() { return batteryLevel; }
	public void setBatteryLevel( int batteryLevel ) { this.batteryLevel = batteryLevel; }
	
	public String getDisplayOn() { return displayOn; }
	public void setDisplayOn( String displayOn ) { this.displayOn = displayOn; }
	
	public void print() {
		System.out.println( "[AdbDevice] ================================" );
		System.out.println( "[AdbDevice] serialNumber : " + serialNumber );
		System.out.println( "[AdbDevice] model : " + model );
		System.out.println( "[AdbDevice] os_ver : Android " + os_ver );
		System.out.println( "[AdbDevice] status : " + status );
		System.out.println( "[AdbDevice] orientation : " + getOrientationText( orientation ));
		System.out.println( "[AdbDevice] battery level : " + batteryLevel );
		System.out.println( "[AdbDevice] display : " + displayOn );
		System.out.println( "[AdbDevice] ================================" );
	}
	
	/**
	 * @param orientation
	 * @return
	 */
	public String getOrientationText() { return getOrientationText(orientation); } 
	public static String getOrientationText( int orientation ) {
		String text = "PORTRAIT";
		switch (orientation) {
	    case 0 : text = "PORTRAIT"; 	break;
	    case 1 : text = "LANDSCAPE"; break;
	    case 2 : text = "REVERSE_PORTRAIT"; break;
	    case 3 : text = "REVERSE_LANDSCAPE"; break;
	    default: text = "PORTRAIT"; break;
	    }
		return text;
	}
}
