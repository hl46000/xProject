package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


public class BithumbMyBalanceInfo extends BithumbBaseClass {
	double balances[] = new double[CURRENCY_DEF.MAX_CURRENCY];
	long krw = 0;
	
	public long getKrw() { return krw; }
	public double [] getBalances() { return balances; }
	
	public String toInfoString() {
		String ret = String.format( "KRW : %d", krw );
		for( int i = 0; i < CURRENCY_DEF.MAX_CURRENCY; i++ ) {
			ret += String.format( ", %s : %f", CURRENCY_DEF.strCurrencies[i].toUpperCase(), balances[i]);
		}
		return ret;
	}
	
	@Override
	protected String getApiUri() {
		return "/info/balance";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("currency", "ALL");
		rgParams.put("payment_currency", "KRW");
		
		return rgParams;
	}

	@Override
	protected void parser(JSONObject jsonData) {
		krw = (Long) jsonData.get("total_krw");
		
		for( int i = 0; i < CURRENCY_DEF.MAX_CURRENCY; i++ ) {
			Object objValue = jsonData.get( "total_" + CURRENCY_DEF.strCurrencies[i].toLowerCase() );
			if( objValue != null ) {
				balances[i] = Double.valueOf( (String) objValue );
			} else {
				balances[i] = 0.0f;
			}
		}
	}
}
