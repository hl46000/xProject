package com.purehero.bithumb.api;

import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;

/**
 * @author MY
 *
 *

https://api.bithumb.com/public/orderbook/{currency}더보기 bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
{currency} = BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC), ALL(전체


[Returned Example]
{
    "status"    : "0000",
    "data"      : {
        "timestamp"         : 1417142049868,
        "order_currency"    : "BTC",
        "payment_currency"  : "KRW",
        "bids": [
            {
                "quantity"  : "6.1189306",
                "price"     : "504000"
            },
            {
                "quantity"  : "10.35117828",
                "price"     : "503000"
            }
        ],
        "asks": [
            {
                "quantity"  : "2.67575",
                "price"     : "506000"
            },
            {
                "quantity"  : "3.54343",
                "price"     : "507000"
            }
        ]
    }
}

[Returned Value Description]
	Key Name			Description
	status				결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	timestamp			현재 시간 Timestamp
	order_currency		주문 화폐단위
	payment_currency	결제 화폐단위
	bids				구매요청
	asks				판매요청
	quantity			Currency 수량
	price				1Currency당 거래금액


[Request Parameters]
	Parameter Name	Data Type	Description
	group_orders	Int			Value : 0 또는 1 (Default : 1)
	count			Int			Value : 1 ~ 50 (Default : 20), ALL : 1 ~ 5(Default : 5)
 *
 */
public class BithumbOrderBookALL extends BithumbBaseClass {
	OrderBookData lastOrderBookDatas [] 	= new OrderBookData[ CURRENCY_DEF.MAX_CURRENCY ];
	Date timestampe = new Date();
	
	public BithumbOrderBookALL() {
		for( int i = 0; i < lastOrderBookDatas.length; i++ ) {
			lastOrderBookDatas[i] = new OrderBookData();
		}
	}
	
	@Override
	protected String getApiUri() {
		return "/public/orderbook/ALL";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		return null;	// public API는 null을 반환한다. 
	}
	
	@Override
	protected void parser( JSONObject jsonData ){
		String strTimestamp = ( String ) jsonData.get( "timestamp" );
		timestampe.setTime( Long.valueOf( strTimestamp ));
		
		for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
			JSONObject jsonCurrency = ( JSONObject ) jsonData.get( CURRENCY_DEF.strCurrencies[idxCurrency] );
			if( jsonCurrency == null ) continue;
			
			OrderBookData orderBookData = lastOrderBookDatas[idxCurrency];
			orderBookData.clear();			
			orderBookData.bids( ( JSONArray ) jsonCurrency.get( "bids" ));
			orderBookData.asks( ( JSONArray ) jsonCurrency.get( "asks" ));
		}		
	}
	
	public OrderBookData[] getOrderBookDatas() {
		return lastOrderBookDatas;
	}
}
