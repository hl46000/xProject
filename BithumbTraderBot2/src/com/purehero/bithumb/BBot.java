package com.purehero.bithumb;

import com.purehero.bithumb.api.APIPrivateInfoBalance;
import com.purehero.bithumb.api.APIPublicOrderBook;
import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.Currency;


public class BBot {

	public static void main(String[] args) {
		new BBot().run();
	}

	private BithumbAPI bithumbAPI = new BithumbAPI();
	private APIPrivateInfoBalance pMyBalance = new APIPrivateInfoBalance( bithumbAPI );
	
	private Currency tradeCurrencys [] = { Currency.XMR, Currency.DASH }; 
	private APIPublicOrderBook orderBooks [] = new APIPublicOrderBook[ tradeCurrencys.length ]; 
	
	private void run() {
		for( int i = 0; i < tradeCurrencys.length; i++ ) {
			orderBooks[i] = new APIPublicOrderBook( bithumbAPI, tradeCurrencys[i] ); 
		}
		
		pMyBalance.update();
		pMyBalance.print();
		
		for( int i = 0; i < tradeCurrencys.length; i++ ) {
			orderBooks[i].update();
			orderBooks[i].print();
		}
	}
	
	
}
