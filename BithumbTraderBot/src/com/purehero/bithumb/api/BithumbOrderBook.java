package com.purehero.bithumb.api;

import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;

public class BithumbOrderBook extends BithumbBaseClass {
	OrderBookData lastOrderBookDatas [] 	= new OrderBookData[ CURRENCY_DEF.MAX_CURRENCY ];
	Date timestampe = new Date();
	
	@Override
	protected String getApiUri() {
		// "https://api.bithumb.com/public/orderbook/ALL" 에서 "https://api.bithumb.com" 을 제외한 uri 을 반환하면 된다.  
		return "/public/orderbook/ALL";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		return null;	// public API는 null을 반환한다. 
	}
	
	@Override
	protected void parser( JSONObject jsonData ){
		// "data":{"timestamp":"1504363928260","payment_currency":"KRW",
		//			"BTC":{"order_currency":"BTC","bids":[{"quantity":"1.81540000","price":"5050000"},{"quantity":"1.09900000","price":"5049000"},{"quantity":"0.10000000","price":"5045000"},{"quantity":"2.97100000","price":"5040000"},{"quantity":"0.00970000","price":"5032000"}],"asks":[{"quantity":"3.29380000","price":"5055000"},{"quantity":"0.30220000","price":"5056000"},{"quantity":"3.31840000","price":"5080000"},{"quantity":"0.20960000","price":"5082000"},{"quantity":"1.00000000","price":"5086000"}]},
		//			"ETH":{"order_currency":"ETH","bids":[{"quantity":"0.90400000","price":"379200"},{"quantity":"147.86320000","price":"379150"},{"quantity":"74.23820000","price":"379100"},{"quantity":"3.35000000","price":"379000"},{"quantity":"15.00000000","price":"378550"}],"asks":[{"quantity":"8.41790000","price":"379250"},{"quantity":"27.00000000","price":"379850"},{"quantity":"105.72513158","price":"379900"},{"quantity":"0.00000000","price":"380350"},{"quantity":"2.00000000","price":"381000"}]},
		//			"DASH":{"order_currency":"DASH","bids":[{"quantity":"0.99760000","price":"386050"},{"quantity":"1.00000000","price":"383550"},{"quantity":"14.58500000","price":"383500"},{"quantity":"3.00000000","price":"383000"},{"quantity":"11.33140000","price":"382600"}],"asks":[{"quantity":"2.06610000","price":"386100"},{"quantity":"6.65756107","price":"386400"},{"quantity":"25.78570000","price":"386500"},{"quantity":"2.50000000","price":"386600"},{"quantity":"31.68500000","price":"386800"}]},
		//			"LTC":{"order_currency":"LTC","bids":[{"quantity":"855.67360000","price":"84540"},{"quantity":"148.75251006","price":"84500"},{"quantity":"592.41700000","price":"84400"},{"quantity":"7.69210000","price":"84360"},{"quantity":"0.10000000","price":"84310"}],"asks":[{"quantity":"147.48529261","price":"84580"},{"quantity":"122.10290000","price":"84780"},{"quantity":"79.22369781","price":"84830"},{"quantity":"107.49140000","price":"84970"},{"quantity":"540.70430000","price":"85000"}]},
		//			"ETC":{"order_currency":"ETC","bids":[{"quantity":"44.13600000","price":"21405"},{"quantity":"1954.99410000","price":"21210"},{"quantity":"303.10420000","price":"21190"},{"quantity":"241.42630000","price":"21010"},{"quantity":"117.41590000","price":"21000"}],"asks":[{"quantity":"260.39493189","price":"21500"},{"quantity":"1.22470000","price":"21520"},{"quantity":"46.00000000","price":"21545"},{"quantity":"460.49760000","price":"21580"},{"quantity":"144.17220000","price":"21620"}]},
		//			"XRP":{"order_currency":"XRP","bids":[{"quantity":"427783.41560000","price":"249"},{"quantity":"493617.15810000","price":"248"},{"quantity":"191232.96310000","price":"247"},{"quantity":"724998.38590000","price":"246"},{"quantity":"2101064.48900000","price":"245"}],"asks":[{"quantity":"238385.29280000","price":"250"},{"quantity":"304667.35433000","price":"251"},{"quantity":"352108.82630800","price":"252"},{"quantity":"339677.41588500","price":"253"},{"quantity":"355141.72518600","price":"254"}]},
		//			"BCH":{"order_currency":"BCH","bids":[{"quantity":"25.74100000","price":"631000"},{"quantity":"2.74340000","price":"630200"},{"quantity":"6.72720000","price":"630000"},{"quantity":"16.39520000","price":"629400"},{"quantity":"6.01000000","price":"629100"}],"asks":[{"quantity":"8.34353229","price":"635000"},{"quantity":"0.50000000","price":"639000"},{"quantity":"14.61830000","price":"639100"},{"quantity":"21.98740000","price":"640100"},{"quantity":"16.36890000","price":"640800"}]},
		//			"XMR":{"order_currency":"XMR","bids":[{"quantity":"22.89510000","price":"141010"},{"quantity":"74.50189078","price":"141000"},{"quantity":"20.72620000","price":"140520"},{"quantity":"100.00000000","price":"140500"},{"quantity":"18.74840000","price":"140210"}],"asks":[{"quantity":"0.99920000","price":"141060"},{"quantity":"27.26810000","price":"142600"},{"quantity":"75.31390000","price":"142790"},{"quantity":"44.09250000","price":"142800"},{"quantity":"9.98580000","price":"142850"}]}}
		String strTimestamp = ( String ) jsonData.get( "timestamp" );
		timestampe.setTime( Long.valueOf( strTimestamp ));
		
		for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
			JSONObject jsonCurrency = ( JSONObject ) jsonData.get( CURRENCY_DEF.strCurrencies[idxCurrency] );
			if( jsonCurrency == null ) continue;
			
			OrderBookData orderBookData = new OrderBookData();
			orderBookData.bids( ( JSONArray ) jsonCurrency.get( "bids" ));
			orderBookData.asks( ( JSONArray ) jsonCurrency.get( "asks" ));
			
			lastOrderBookDatas[idxCurrency] = orderBookData;
		}
	}
	
	public OrderBookData[] getOrderBookDatas() {
		return lastOrderBookDatas;
	}
}
