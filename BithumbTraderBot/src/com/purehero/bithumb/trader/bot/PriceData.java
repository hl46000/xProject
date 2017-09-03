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
	
	private int currencyLastPrice;
	public int getCurrencyLastPrice() {
		return currencyLastPrice;
	}
	public void setCurrencyLastPrice( int currencyLastPrice ) {
		this.currencyLastPrice = currencyLastPrice;
	}
	public String getCurrencyLastPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyLastPrice );		
	}
	
	private int currencyPrice;
	public int getCurrencyPrice() {
		return currencyPrice;
	}
	public void setCurrencyPrice( int currencyPrice ) {
		this.currencyPrice = currencyPrice;
	}
	public String getCurrencyPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyPrice );
	}
}


