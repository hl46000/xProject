package com.bithumb.sample;

import java.util.HashMap;

import com.purehero.bithumb.util.Api_Client;

public class Main {
	
	private final static String apk_key 	= "64346e7c6622346faa0cff0ac46c73a3"; 
	private final static String secure_key = "79639ec221ba0c958eef5d66a7f8fdaa";
	private final static Api_Client api = new Api_Client( apk_key, secure_key );
	
    public static void main(String args[]) {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("order_currency", "BTC");
		rgParams.put("payment_currency", "KRW");
	
		try {
		    String result = api.callApi("/info/balance", rgParams);
		    System.out.println(result);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
    }
}

