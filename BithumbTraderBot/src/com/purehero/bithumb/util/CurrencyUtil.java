package com.purehero.bithumb.util;

import java.text.DecimalFormat;

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
	
	private static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,###");
	public static String getIntegerToFormatString( int value ) {
		return decimalFormat.format( value );
	}
	
	public static String getDoubleToSellFormatString( double value ) {
		return String.format( "%.4f", value );
	}
}
