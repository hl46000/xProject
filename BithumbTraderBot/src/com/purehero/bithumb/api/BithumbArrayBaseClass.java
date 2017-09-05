package com.purehero.bithumb.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.Util;

public abstract class BithumbArrayBaseClass extends BithumbBaseClass {
	public boolean requestAPI( Api_Client api ) {
		responseJsonString = null;
		try {
			Util.sleepMillisecond( 100 );	
			// ������ Private API ȣ���� 1�ʿ� 10ȸ�� �����Ѵ�.
			// �̸� ���� �������� �ޱ� ������ 1�ʿ� 10���̻� ȣ����� �ʰ� �ϱ� ����  API 1ȸ ȣ��� 100ms �� ������ ���� �Ѵ�.  
			
			responseJsonString = api.callApi( getApiUri(), getApiRequestParams() );
			JSONArray jsonData = preParser( responseJsonString );
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
	
	private JSONArray preParser( String jsonString ) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = ( JSONObject ) parser.parse( jsonString );
	
		int status = Integer.valueOf(( String ) json.get("status"));
		if( status != 0 ) {
			System.err.println( jsonString);
			return null;
		}
		
		return ( JSONArray ) json.get( "data" );
	}
	
	@Override
	protected void parser(JSONObject jsonData) {}
	protected abstract void parser( JSONArray jsonData );
}
