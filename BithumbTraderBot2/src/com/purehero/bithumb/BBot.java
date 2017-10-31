package com.purehero.bithumb;

import com.purehero.bithumb.api.APIPrivateInfoBalance;
import com.purehero.bithumb.api.APIPublicOrderBook;
import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.Currency;


public class BBot {

	public static void main(String[] args) {
		new BBot().init().run();
	}

	private BithumbAPI bithumbAPI = new BithumbAPI();
	private APIPrivateInfoBalance pMyBalance = new APIPrivateInfoBalance( bithumbAPI );
	
	private Currency tradeCurrencys [] = { Currency.BTC, Currency.ETC, Currency.ETH, Currency.XMR, Currency.DASH, Currency.LTC, Currency.XRP, Currency.ZEC, Currency.QTUM }; 
	//private Currency tradeCurrencys [] = { Currency.LTC };
	private APIPublicOrderBook orderBooks [] = new APIPublicOrderBook[ tradeCurrencys.length ]; 
	
	private BBot init() {
		for( int i = 0; i < tradeCurrencys.length; i++ ) {
			orderBooks[i] = new APIPublicOrderBook( bithumbAPI, tradeCurrencys[i] ); 
		}
		
		pMyBalance.update();
		return this;
	}
	
	private void run() {
		bithumbAPI.setEnabledLog( false );
		
		for( int i = 0; i < tradeCurrencys.length; i++ ) {
			new BithumbTrader( bithumbAPI, pMyBalance, tradeCurrencys[i] ).start();			
		}
	}
	
}
