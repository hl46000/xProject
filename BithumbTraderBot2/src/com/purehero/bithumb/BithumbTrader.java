package com.purehero.bithumb;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import com.purehero.bithumb.api.APIPrivateInfoBalance;
import com.purehero.bithumb.api.APIPublicOrderBook;
import com.purehero.bithumb.api.BithumbAPI;
import com.purehero.bithumb.api.Currency;
import com.purehero.bithumb.api.Util;

public class BithumbTrader extends Thread {
	private static final long TEN_MINITUE_MILLISEC = 600000;
	private static final long THIRTY_MINITUE_MILLISEC = 600000 * 3;
	
	private Deque<Integer> priceHistory 		= new LinkedList<Integer>();
	private Deque<Long> priceHistoryTime 		= new LinkedList<Long>();
	private Deque<Boolean> priceUpDownHistory	= new LinkedList<Boolean>();
	
	private int asksPriceLine 		= Integer.MIN_VALUE;		// 구매를 한후 판매를 하기위한 기준 금액
	private int asksOrderPrice 	= Integer.MIN_VALUE;		// 구매를 한 금액
	private double asksOrderUnits = Double.MIN_VALUE;		// 구매 개수
	
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

	int asksLowestPrice  = 0;		// 가장 낮은 판매 금액
	int bidsHighestPrice = 0;		// 가장 높은 구매 금액
	int beforeAsksLowestPrice = 0;
	
	@Override
	public void run() {
		
		System.out.println( String.format( "Started %s trading", currency.getName()));
		
		while( true ) {
			
			try { Thread.sleep( 100 ); } catch (InterruptedException e) { e.printStackTrace(); }
				
			orderBooks.update();
		
			asksLowestPrice  = orderBooks.getAsksLowestPrice();			// 가장 낮은 판매 금액
			bidsHighestPrice = orderBooks.getBidsHighestPrice();		// 가장 높은 구매 금액
			
			if( beforeAsksLowestPrice == asksLowestPrice ) continue;	 // 바로전 가격과 동일하다면 처리할 필요가 없다.
			beforeAsksLowestPrice = asksLowestPrice;
			
			// 가격 변동 추이를 방향을 결정하기 위한 데이터 
			priceUpDownHistory.add( asksLowestPrice > beforeAsksLowestPrice );
			if( priceUpDownHistory.size() > 10 ) {
				priceUpDownHistory.removeFirst();
			}
			
			long currentTime = System.currentTimeMillis();
			priceHistoryTime.add( currentTime );
			priceHistory.add( asksLowestPrice );
			
			if( currentTime - priceHistoryTime.getFirst() > THIRTY_MINITUE_MILLISEC ) {
				priceHistory.removeFirst();
				priceHistoryTime.removeFirst();
			}
			
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
	
	private boolean bidsTrading() {
		if( asksPriceLine != Integer.MIN_VALUE ) return true;		// 판매 기준 금액이 설정되면 구매가 완료된 것으로 본다. 
													// 판매 Trading 을 수행한다. 
		
		// 판매 대기금액을 기준으로 구매를 결정한다.
		// 판매대기 금액중 가장 낮은 금액의 변동폭을 기록한다. ( 판매 시장가격 ) 
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
		if( minPrice + unitPrice > asksLowestPrice && priceUpDownValue() > 0 ) {
			// 구매를 한다.
			System.out.println( String.format( "%s 구매 가격 : %s", currency.getKorName(), Util.intergerToPriceString( asksLowestPrice )));
			System.out.println( String.format( "사용가능한 KRW : %s", Util.intergerToPriceString( pMyBalance.getAvailableKrw())));
			
			asksOrderUnits = pMyBalance.getAvailableKrw() / ( double ) asksLowestPrice;
			System.out.println( String.format( "구매 개수 : %f", asksOrderUnits ));
			
			pMyBalance.setAvailableKrw( 0 );
			
			asksPriceLine 	= maxPrice - unitPrice;
			asksOrderPrice 	= asksLowestPrice; 
			
		} else {
			asksPriceLine = Integer.MIN_VALUE;
		}
			
		return asksPriceLine != Integer.MIN_VALUE;
	}
	
	/**
	 * 현재 가격에 하향인지 상향인지를 반환한다. 
	 * 
	 * @return
	 */
	private int priceUpDownValue() {
		int ret = 0;
		for( Boolean upDown : priceUpDownHistory ) {
			if( upDown ) {
				++ ret;
			} else {
				++ ret;
			}
		}
		return ret;
	}

	private void asksTrading() {
		// 구매 대기금액을 기준으로 판매를 결정한다.
		// 최대 구매대기 금액이 판매 기준 금액 이상이고, 
		// 가격 방향이 하락이면 판매를 한다. 
		if( bidsHighestPrice > asksPriceLine && priceUpDownValue() < 0 ) {
			System.out.println( String.format( "%s 판매 가격 : %s", currency.getKorName(), Util.intergerToPriceString( bidsHighestPrice )));
			System.out.println( String.format( "판매 개수 : %f", asksOrderUnits ));
			
			pMyBalance.setAvailableKrw((int)( asksOrderUnits * bidsHighestPrice ));
			
			System.out.println( String.format( "판매 한 KRW : %s", Util.intergerToPriceString( pMyBalance.getAvailableKrw() )));
			
			// 다시 구매를 할수 있도록 판매기준 금액을 초기화 한다. 
			asksPriceLine = Integer.MIN_VALUE;
		}
	}
}
