package com.purehero.bithumb.api;

import java.util.HashMap;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.Currency;
import com.purehero.bithumb.util.Util;

public abstract class BithumbBaseClass {
	protected String responseJsonString = null;
	
	public String getResponseJSonString() {
		return responseJsonString;
	}
	
	public synchronized boolean requestAPI( Api_Client api ) {
		responseJsonString = null;
		try {
			Util.sleepMillisecond( 100 );	
			// 빗썸은 Private API 호출을 1초에 10회로 제한한다.
			// 이를 어기면 불이익을 받기 때문에 1초에 10번이상 호출되지 않게 하기 위해  API 1회 호출당 100ms 을 강제로 쉬게 한다.  
			
			responseJsonString = api.callApi( getApiUri(), getApiRequestParams() );
			JSONObject jsonData = preParser( responseJsonString );
			if( jsonData == null ) return false;
			
			parser( jsonData );
		} catch (Exception e) {
		    e.printStackTrace();
		    if( responseJsonString != null ) {
		    	System.err.println( responseJsonString );
		    }
		    responseJsonString = null;
		}
		
		return responseJsonString != null;
	}
	
	private JSONObject preParser( String jsonString ) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = ( JSONObject ) parser.parse( jsonString );
	
		int status = Integer.valueOf(( String ) json.get("status"));
		if( status != 0 ) {
			String errMessage = ( String ) json.get("message");
			Alert alert = new Alert(AlertType.NONE, errMessage, ButtonType.OK );
			alert.showAndWait();
			
			System.err.println( jsonString );
			System.err.println( errMessage );			
			return null;
		}
		
		return ( JSONObject ) json.get( "data" );
	}
	
	protected abstract String getApiUri();
	protected abstract HashMap<String, String> getApiRequestParams();
	protected abstract void parser( JSONObject jsonData );
	
	int currency = CURRENCY_DEF.BCH;
	public void setCurrency( Currency currency ) {
		this.currency = currency.ordinal();
	}
	public void setCurrency( int currency ) {
		this.currency = currency;
	}
}
