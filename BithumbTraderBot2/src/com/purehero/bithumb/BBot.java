package com.purehero.bithumb;

import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.BithumbApiType;
import com.purehero.bithumb.api.Currency;


public class BBot {

	public static void main(String[] args) {
		new BBot().run();
	}

	private BithumbAPI bithumbAPI = new BithumbAPI();
	private void run() {
		String result = bithumbAPI.request( BithumbApiType.PRIVATE_INFO_BALANCE, Currency.LTC, null );
		
		System.out.println( result );
	}
	
	
}
