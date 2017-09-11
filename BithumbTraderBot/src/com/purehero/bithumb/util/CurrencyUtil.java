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
	
	private static DecimalFormat unitsDecimalFormat = new DecimalFormat("0.00000000  ");
	public static String getUnitsToFormatString( double units ) {
		return unitsDecimalFormat.format( units );
	}
	
	public static String getDoubleToSellFormatString( double value ) {
		return String.format( "%.4f", Math.floor( value * 10000.0d ) / 10000.0d );
	}
	
	public static double getCurrencyUnits( int currency, double units ) {
		switch( currency ) {
		default :
		case CURRENCY_DEF.XMR : 
			return Math.floor( units * 10000d ) / 10000d;
		}
	}
}
