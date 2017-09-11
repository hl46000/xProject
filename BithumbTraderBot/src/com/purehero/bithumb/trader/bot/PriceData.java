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
		return CurrencyUtil.getUnitsToFormatString( currencyUnits );
	}
	
	// �ü�
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
	///////////////////////////////////////////////////
	
	// ���� ���� �ݾ�
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
	/////////////////////////////////////
	
	// ���� ������ ���� �ݾ�
	private int currencyLastBuyPrice = 0;
	public int getCurrencyLastBuyPrice() { return currencyLastBuyPrice; }
	public void setCurrencyLastBuyPrice( int currencyLastBuyPrice ) { this.currencyLastBuyPrice = currencyLastBuyPrice; }
	
	// ���� ������ �Ǹ� �ݾ�
	private int currencyLastSellPrice = 0;
	public int getCurrencyLastSellPrice() { return currencyLastSellPrice; }
	public void setCurrencyLastSellPrice( int currencyLastSellPrice ) {
		this.currencyLastSellPrice = currencyLastSellPrice;
	}
	
	
	public String getCurrencyLastBuyPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyLastBuyPrice ) + String.format( "(%.2f%%)", ( getCurrencyPriceRate( currencyLastBuyPrice ) * 100 + 5 ) / 100.0d );
	}
	//////////////////////////////////////////////////////
	
	
	
	public String getCurrencyLastSellPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( currencyLastSellPrice ) + String.format( "(%.2f%%)", ( getCurrencyPriceRate( currencyLastSellPrice ) * 100 + 5 ) / 100.0d );
	}
	////////////////////////////////////////////////////
	
	private double getCurrencyPriceRate( double price ) { 
		if( price == 0 || currencyLastPrice == 0 ) return 0;
		double dVal = currencyLastPrice - price;
		dVal *= 100.0d;
		dVal /= price;
		return dVal;
	}
}


