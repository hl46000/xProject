package com.purehero.bithumb.trader.bot;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import com.purehero.bithumb.api.BithumbLastTicker;
import com.purehero.bithumb.api.BithumbMyBalanceInfo;
import com.purehero.bithumb.api.BithumbOrderBook;
import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.CURRENCY_DEF;

public class TraderBotThread extends Thread implements Runnable {
	private Api_Client api;
	private ObservableList<PriceData> priceTableDatas;
	
	private BithumbMyBalanceInfo balanceInfo = new BithumbMyBalanceInfo(); 
	//private BithumbMyTicker tickerInfo = new BithumbMyTicker();
	private BithumbLastTicker requestLastTicker = new BithumbLastTicker();
	private BithumbOrderBook  requestOrderBook 	= new BithumbOrderBook();
	private boolean threadFlag = true;
	
	private int lastPrices [] = null;
	
	@Override
	public void run() {
		balanceInfo.requestAPI( api );
		
		while( threadFlag ) {
			if( requestLastTicker.requestAPI( api )) {
				System.out.println( requestLastTicker.toInfoString() );
				lastPrices = requestLastTicker.getLastPriceInfos();
				
				/*
				sleepMillisecond( 300 );
				if( requestOrderBook.requestAPI( api )) {
					System.out.println( requestOrderBook.getResponseJSonString() );
				}
				OrderBookData lastOrderBookDatas [] = requestOrderBook.getOrderBookDatas();
				*/
			}
			Platform.runLater( uiUpdateRunnable );
			sleepMillisecond( 500 );
		}
	}
	
	Runnable uiUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			priceTableDatas.clear();
			
			double balances[] = balanceInfo.getBalances();
			for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
				PriceData priceData = new PriceData();
				priceData.setCurrencyName( CURRENCY_DEF.strCurrencies[ idxCurrency ] );
				priceData.setCurrencyUnits( balances[ idxCurrency ] );
				priceData.setCurrencyLastPrice( lastPrices[ idxCurrency ] );
				priceData.setCurrencyPrice((int)( balances[ idxCurrency ] * lastPrices[ idxCurrency ] ));
				
				priceTableDatas.add( priceData );
			}
		}
	};
	
	private void sleepMillisecond( int millisecond ) {
		try { Thread.sleep( millisecond ); } catch (InterruptedException e) { e.printStackTrace();}
	}
	
	public void startThread( Api_Client api, ObservableList<PriceData> priceTableDatas ) {
		this.api = api;
		this.priceTableDatas = priceTableDatas;
		
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
