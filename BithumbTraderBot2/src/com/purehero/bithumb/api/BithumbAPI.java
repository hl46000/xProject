package com.purehero.bithumb.api;

import java.util.HashMap;
import java.util.Map;

public class BithumbAPI {
	private final static Api_Client api_client = new Api_Client( APIKey.getAPIKey(), APIKey.getSecureKey() );
	
	public String request( BithumbApiType apiType, Currency currency, Map<String,String> params ) {
		switch( apiType ) {
		case PUBLIC_TICKER 		:
		case PUBLIC_ORDERBOOK 	:
		case PUBLIC_RECENT_TRANSACTIONS 	:
			return api_client.callApi( apiType.getUrl( currency ), params );
			
		case PRIVATE_INFO_ACCOUNT :
		case PRIVATE_INFO_BALANCE :
		case PRIVATE_INFO_WALLET_ADDRESS :
		case PRIVATE_INFO_TICKER :
			return api_client.callApi( apiType.getUrl( currency ), addCurrencyToParam( currency, params ));
		}
		
		return null;
	}
	
	private Map<String,String> addCurrencyToParam( Currency currency, Map<String,String> params ) {
		if( params == null ) {
			params = new HashMap<String,String>();
		}
		
		updateCurrency( "currency", currency, params );
		updateCurrency( "order_currency", currency, params );
		
		return params;
	}

	private void updateCurrency(String strKey, Currency currency, Map<String, String> params) {
		if( params.containsKey( strKey )) {
			params.remove( strKey );
		}
		
		params.put( strKey, currency.getSymbol());		
	}
}
