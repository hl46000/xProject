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
 *
 */
public class BithumbLastTicker extends BithumbBaseClass {
	int lastPriceInfos [] 	= new int[ CURRENCY_DEF.MAX_CURRENCY ];  
	Date lastPriceDate = new Date();
	
	public int[] getLastPriceInfos() { return lastPriceInfos; }
	public Date  getLastPriceDate()  { return lastPriceDate; }
	
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
			lastPriceInfos[idxCurrency] = CurrencyUtil.priceStringToInteger( strPrice );			
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
