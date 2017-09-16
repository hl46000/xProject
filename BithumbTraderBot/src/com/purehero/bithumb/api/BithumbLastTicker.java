package com.purehero.bithumb.api;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * bithumb �ŷ��� ������ �ŷ� ����
 * 
 * @author MY

https://api.bithumb.com/public/ticker/{currency}		bithumb �ŷ��� ������ �ŷ� ����
* {currency} = BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC), ALL(��ü)

[Returned Example]
{
    "status": "0000",
    "data": {
        "opening_price" : "504000",
        "closing_price" : "505000",
        "min_price"     : "504000",
        "max_price"     : "516000",
        "average_price" : "509533.3333",
        "units_traded"  : "14.71960286",
        "volume_1day"   : "14.71960286",
        "volume_7day"   : "15.81960286",
        "buy_price"     : "505000",
        "sell_price"    : "504000",
        "date"          : 1417141032622
    }
}

[Returned Value Description]
	Key Name		Description
	status			��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	opening_price	�ֱ� 24�ð� �� ���� �ŷ��ݾ�
	closing_price	�ֱ� 24�ð� �� ������ �ŷ��ݾ�
	min_price		�ֱ� 24�ð� �� ���� �ŷ��ݾ�
	max_price		�ֱ� 24�ð� �� �ְ� �ŷ��ݾ�
	average_price	�ֱ� 24�ð� �� ��� �ŷ��ݾ�
	units_traded	�ֱ� 24�ð� �� Currency �ŷ���
	volume_1day		�ֱ� 1�ϰ� Currency �ŷ���
	volume_7day		�ֱ� 7�ϰ� Currency �ŷ���
	buy_price		�ŷ� ���� �ְ� ���Ű�
	sell_price		�ŷ� ���� �ּ� �ǸŰ�
	date			���� �ð� Timestamp


 */
public class BithumbLastTicker extends BithumbBaseClass {
	int lastPriceInfos [] 	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// �ֱ� 24�ð� �� ������ �ŷ��ݾ�  
	int lastMinPriceInfos[] = new int[ CURRENCY_DEF.MAX_CURRENCY ];	// �ֱ� 24�ð� �� ���� �ŷ��ݾ�
	int lastHighestBuyPrice[]	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// �ŷ� ���� �ְ� ���Ű�
	int lastLowestSellPrice[]	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// �ŷ� ���� �ּ� �ǸŰ�
	Date lastPriceDate = new Date();
	
	public int[] getLastPriceInfos() 	{ return lastPriceInfos; }
	public int[] getLastMinPriceInfos()	{ return lastMinPriceInfos; }
	public int[] getLastHighestBuyPrice()	{ return lastHighestBuyPrice; }
	public int[] getLastLowestSellPrice()	{ return lastLowestSellPrice; }
	public Date  getLastPriceDate()  	{ return lastPriceDate; }
	
	public String toInfoString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss"); 
		
		String ret = String.format( "[%s] ", formatter.format( lastPriceDate ));
		
		for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
			if( idxCurrency != 0 ) ret += ", "; 
        	ret += String.format( "%s(%d)", CURRENCY_DEF.strCurrencies[idxCurrency], lastPriceInfos[idxCurrency] );
		}
		
		return ret;
	}
	
	@Override
	protected void parser(JSONObject jsonData) {
		for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
			JSONObject jsonCurrency = ( JSONObject ) jsonData.get( CURRENCY_DEF.strCurrencies[idxCurrency] );
			if( jsonCurrency == null ) continue;
			
			String strPrice = ( String )jsonCurrency.get( "closing_price" );
			lastPriceInfos[idxCurrency] 	= CurrencyUtil.priceStringToInteger( strPrice );
			
			String strMinPrice = ( String )jsonCurrency.get( "min_price" );
			lastMinPriceInfos[idxCurrency]	= CurrencyUtil.priceStringToInteger( strMinPrice );
			
			String strBuyPrice = ( String )jsonCurrency.get( "buy_price" );
			lastHighestBuyPrice[idxCurrency]	= CurrencyUtil.priceStringToInteger( strBuyPrice );
			
			String strSellPrice = ( String )jsonCurrency.get( "sell_price" );
			lastLowestSellPrice[idxCurrency]	= CurrencyUtil.priceStringToInteger( strSellPrice );
		}
		
		String strDate = ( String ) jsonData.get( "date" );
		
		lastPriceDate.setTime( Long.valueOf( strDate ));
	}
	
	@Override
	protected String getApiUri() {		
		return "/public/ticker/ALL";
	}
	
	@Override
	protected HashMap<String, String> getApiRequestParams() {
		return null;
	}
}
