package com.purehero.bithumb.trader.bot;

import java.text.DecimalFormat;

import com.purehero.bithumb.util.CurrencyUtil;

public class PriceData {
	private String currencyName;
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName( String currencyName ) {
		this.currencyName = currencyName;
	}
	
	private double currencyUnits;
	public double getCurrencyUnits() {
		return currencyUnits;
	}
	public void setCurrencyUnits( double currencyUnits ) {
		this.currencyUnits = currencyUnits;
	}
	public String getCurrencyUnitsString() {
		DecimalFormat decimalFormat = new DecimalFormat("0.##########");
		return decimalFormat.format(currencyUnits);
	}
	
	private int currencyLastPrice = 0;
	public int getCurrencyLastPrice() {
		return currencyLastPrice;
	}
	public void setCurrencyLastPrice( int currencyLastPrice ) {
		this.currencyLastPrice = currencyLastPrice;
	}
	public String getCurrencyLastPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyLastPrice );		
	}
	
	private int currencyPrice = 0;
	public int getCurrencyPrice() {
		return currencyPrice;
	}
	public void setCurrencyPrice( int currencyPrice ) {
		this.currencyPrice = currencyPrice;
	}
	public String getCurrencyPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyPrice );
	}
	
	private int currencyBasePrice = 0;
	public int getCurrencyBasePrice() {
		return currencyBasePrice;
	}
	public void setCurrencyBasePrice( int currencyBasePrice ) {
		this.currencyBasePrice = currencyBasePrice;
	}
	public String getCurrencyBasePriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyBasePrice );
	}
	
	public double getRate() { 
		if( currencyBasePrice == 0 || currencyLastPrice == 0 ) return 0;
		double dVal = currencyLastPrice - currencyBasePrice;
		dVal *= 100.0d;
		dVal /= currencyBasePrice;
		return dVal;
	}
	public String getRateString() { 
		return String.format( "%.2f%%", ( getRate() * 100 + 5 ) / 100.0d );
	}
}


