package com.purehero.bithumb.trader.bot;

import com.purehero.bithumb.api.BithumbLastTicker;
import com.purehero.bithumb.api.BithumbOrderBook;
import com.purehero.bithumb.api.OrderBookData;
import com.purehero.bithumb.util.Api_Client;

public class TraderBotThread extends Thread implements Runnable {
	private final Api_Client api;
	
	//private BithumbMyBalanceInfo balanceInfo = new BithumbMyBalanceInfo(); 
	//private BithumbMyTicker tickerInfo = new BithumbMyTicker();
	private BithumbLastTicker requestLastTicker = new BithumbLastTicker();
	private BithumbOrderBook  requestOrderBook 	= new BithumbOrderBook();
	private boolean threadFlag = true;
	
	public TraderBotThread(Api_Client api) {
		this.api = api;
	}

	@Override
	public void run() {
		while( threadFlag ) {
			if( requestLastTicker.requestAPI( api )) {
				System.out.println( requestLastTicker.toInfoString() );
				int lastPrices[] = requestLastTicker.getLastPriceInfos();
				
				sleepMillisecond( 300 );
				if( requestOrderBook.requestAPI( api )) {
					System.out.println( requestOrderBook.getResponseJSonString() );
				}
				OrderBookData lastOrderBookDatas [] = requestOrderBook.getOrderBookDatas();
			}

			sleepMillisecond( 500 );
		}
	}
	
	private void sleepMillisecond( int millisecond ) {
		try { Thread.sleep( millisecond ); } catch (InterruptedException e) { e.printStackTrace();}
	}
	
	public void startThread() {
		start();
	}
	
	public void stopThread() {
		threadFlag = false;
		try {
			join( 1000 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
