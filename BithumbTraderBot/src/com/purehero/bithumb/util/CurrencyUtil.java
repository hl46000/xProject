package com.purehero.bithumb.util;

public class CurrencyUtil {
	
	public static int priceStringToInteger( String strPrice ) {
		int nPrice = -1;
		if( strPrice.indexOf(".") != -1 ) {
			nPrice = Double.valueOf( strPrice ).intValue();
		} else {
			nPrice = Integer.valueOf( strPrice );
		}
		
		return nPrice;
	}
}
