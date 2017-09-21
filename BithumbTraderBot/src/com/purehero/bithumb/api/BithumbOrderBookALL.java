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

https://api.bithumb.com/public/orderbook/{currency}������ bithumb �ŷ��� ��/���� ��� ��� �Ǵ� �ŷ� �� ���� ����
{currency} = BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC), ALL(��ü


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
	status				��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	timestamp			���� �ð� Timestamp
	order_currency		�ֹ� ȭ�����
	payment_currency	���� ȭ�����
	bids				���ſ�û
	asks				�Ǹſ�û
	quantity			Currency ����
	price				1Currency�� �ŷ��ݾ�


[Request Parameters]
	Parameter Name	Data Type	Description
	group_orders	Int			Value : 0 �Ǵ� 1 (Default : 1)
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
		return null;	// public API�� null�� ��ȯ�Ѵ�. 
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
