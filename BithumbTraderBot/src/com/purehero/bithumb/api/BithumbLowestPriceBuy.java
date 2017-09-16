package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY
 */
public class BithumbLowestPriceBuy extends BithumbBaseClass {
	private BithumbTradePlace tradePlace = new BithumbTradePlace(); 

	@Override
	protected String getApiUri() {
		return tradePlace.getApiUri();
	}
	
	@Override
	public synchronized boolean requestAPI(Api_Client api) {
		return tradePlace.requestAPI(api);
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() { return null; }

	@Override
	protected void parser(JSONObject jsonData) {}
	
	
}
