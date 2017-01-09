package com.purehero.root.checker;


/**
 * @author purehero
 * 
 * Device info data class 
 * 
 */
public class DeviceInfoListData {
	private final String name;
	private final String value;
	
	private int index 		= -1;

	public DeviceInfoListData( String name, String value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value
	 */
	public void setIndex( int value ) {
		index = value;
	}
	
	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}	
}
