package com.purehero.bithumb;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.purehero.bithumb.api.APIPrivateInfoBalance;
import com.purehero.bithumb.api.APIPublicOrderBook;
import com.purehero.bithumb.api.APIPublicOrderBook.OrderBookData;
import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.Currency;
import com.purehero.bithumb.api.Util;

public class BithumbTrader extends Thread {
	private static final long TEN_MINITUE_MILLISEC = 600000;
	private static final long THIRTY_MINITUE_MILLISEC = 600000 * 3;
	
	private Deque<Integer> priceHistory 	= new LinkedList<Integer>();
	private Deque<Long> priceHistoryTime 	= new LinkedList<Long>();
	
	private final BithumbAPI bithumbAPI;
	private final Currency currency;
	private final APIPrivateInfoBalance pMyBalance;
	
	private final APIPublicOrderBook orderBooks;
	
	public BithumbTrader(BithumbAPI bithumbAPI, APIPrivateInfoBalance pMyBalance, Currency currency) {
		this.pMyBalance 	= pMyBalance;
		this.bithumbAPI 	= bithumbAPI;
		this.currency		= currency;
		
		orderBooks = new APIPublicOrderBook( bithumbAPI, currency );
	}

	@Override
	public void run() {
		while( true ) {
			try { Thread.sleep( 100 ); } catch (InterruptedException e) { e.printStackTrace(); }
				
			orderBooks.update();
			if( bidsTrading()) {		// 구매를 위한 추적
				asksTrading();			// 판매를 위한 추적
			}
		}
	}

	/**
	 * 구매를 위한 추적 시작
	 * 구매가 성립되어 있으면 true 반환
	 * 
	 * @return
	 */
	int beforeAsksLowestPrice = 0;
	private boolean bidsTrading() {
		// 판매 대기금액을 기준으로 구매를 결정한다.
		// 판매대기 금액중 가장 낮은 금액의 변동폭을 기록한다. ( 판매 시장가격 ) 
		int asksLowestPrice  = orderBooks.getAsksLowestPrice();			// 가장 낮은 판매 금액
		int bidsHighestPrice = orderBooks.getBidsHighestPrice();		// 가장 높은 구매 금액
		
		if( beforeAsksLowestPrice == asksLowestPrice ) return false;	 // 바로전 가격과 동일하다면 처리할 필요가 없다.
		beforeAsksLowestPrice = asksLowestPrice;
		
		long currentTime = System.currentTimeMillis();
		priceHistoryTime.add( currentTime );
		priceHistory.add( asksLowestPrice );
		
		if( currentTime - priceHistoryTime.getFirst() > THIRTY_MINITUE_MILLISEC ) {
			priceHistory.removeFirst();
			priceHistoryTime.removeFirst();
		}
		
		// 판매 대기 최대 가격과 최소 가격 차이가 지금 가격의 3% 이상인지를 확인한다.
		int maxPrice = Collections.max( priceHistory );
		int minPrice = Collections.min( priceHistory );
		int diffPrice = maxPrice - minPrice;
		
		double percent = diffPrice * 100;
		percent /= asksLowestPrice;
		
		System.out.println( String.format( "%s\t => %s(%s), %5d( %s ~ %s ), %.2f%%", currency.getKorName(), 
				Util.intergerToPriceString( asksLowestPrice ),
				Util.intergerToPriceString( bidsHighestPrice ),				
				diffPrice, 
				Util.intergerToPriceString( maxPrice ), 
				Util.intergerToPriceString( minPrice ), 
				percent ));
		if( percent < 3.0 ) return false;
		
		int unitPrice = diffPrice / 4;
		
		return true;
	}
	
	private void asksTrading() {
		// 구매 대기금액을 기준으로 판매를 결정한다. 
	}
}
