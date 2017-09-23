package com.purehero.bithumb;

import com.purehero.bithumb.api.APIPrivateInfoBalance;
import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.BithumbApiType;
import com.purehero.bithumb.api.Currency;


public class BBot {

	public static void main(String[] args) {
		new BBot().run();
	}

	private BithumbAPI bithumbAPI = new BithumbAPI();
	private APIPrivateInfoBalance pMyBalance = new APIPrivateInfoBalance( bithumbAPI );
	
	private void run() {
		pMyBalance.update();
		pMyBalance.print();		
	}
	
	
}
