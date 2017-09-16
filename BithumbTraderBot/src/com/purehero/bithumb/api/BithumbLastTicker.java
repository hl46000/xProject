package com.purehero.bithumb.api;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * bithumb 거래소 마지막 거래 정보
 * 
 * @author MY

https://api.bithumb.com/public/ticker/{currency}		bithumb 거래소 마지막 거래 정보
* {currency} = BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC), ALL(전체)

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
	status			결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	opening_price	최근 24시간 내 시작 거래금액
	closing_price	최근 24시간 내 마지막 거래금액
	min_price		최근 24시간 내 최저 거래금액
	max_price		최근 24시간 내 최고 거래금액
	average_price	최근 24시간 내 평균 거래금액
	units_traded	최근 24시간 내 Currency 거래량
	volume_1day		최근 1일간 Currency 거래량
	volume_7day		최근 7일간 Currency 거래량
	buy_price		거래 대기건 최고 구매가
	sell_price		거래 대기건 최소 판매가
	date			현재 시간 Timestamp


 */
public class BithumbLastTicker extends BithumbBaseClass {
	int lastPriceInfos [] 	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// 최근 24시간 내 마지막 거래금액  
	int lastMinPriceInfos[] = new int[ CURRENCY_DEF.MAX_CURRENCY ];	// 최근 24시간 내 최저 거래금액
	int lastHighestBuyPrice[]	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// 거래 대기건 최고 구매가
	int lastLowestSellPrice[]	= new int[ CURRENCY_DEF.MAX_CURRENCY ];	// 거래 대기건 최소 판매가
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
