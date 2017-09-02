package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


/**
 * 회원 마지막 거래 정보
 * 
 * @author MY
 *
 */
public class BithumbMyTicker extends BithumbBaseClass {
	public String toInfoString() {
		String ret = "";
		return ret;
	}

	@Override
	protected String getApiUri() {
		return "/info/ticker";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("order_currency", CURRENCY_DEF.strCurrencies[currency.ordinal()] );
		
		return rgParams;
	}

	@Override
	protected void parser(JSONObject jsonData) {
		// TODO Auto-generated method stub
	}
}
