package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.Currency;

public abstract class BithumbBaseClass {
	protected String responseJsonString = null;
	
	public String getResponseJSonString() {
		return responseJsonString;
	}
	
	public boolean requestAPI( Api_Client api ) {
		responseJsonString = null;
		try {
			responseJsonString = api.callApi( getApiUri(), getApiRequestParams() );
			JSONObject jsonData = preParser( responseJsonString );
			if( jsonData == null ) return false;
			
			parser( jsonData );
		} catch (Exception e) {
		    e.printStackTrace();
		    
		    responseJsonString = null;
		}
		
		return responseJsonString != null;
	}
	
	private JSONObject preParser( String jsonString ) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = ( JSONObject ) parser.parse( jsonString );
	
		int status = Integer.valueOf(( String ) json.get("status"));
		if( status != 0 ) {
			System.err.println( jsonString);
			return null;
		}
		
		return ( JSONObject ) json.get( "data" );
	}
	
	protected abstract String getApiUri();
	protected abstract HashMap<String, String> getApiRequestParams();
	protected abstract void parser( JSONObject jsonData );
	
	Currency currency = Currency.BCH;
	public void setCurrency( Currency currency ) {
		this.currency = currency;
	}
}
