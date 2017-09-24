package com.purehero.bithumb.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APIPublicOrderBook {

	List<OrderBookData> asksOrderBook = new ArrayList<OrderBookData>();
	List<OrderBookData> bidsOrderBook = new ArrayList<OrderBookData>();
	
	int asksLowestPrice 	= Integer.MAX_VALUE;
	int asksHighestPrice 	= 0;
	int bidsLowestPrice 	= Integer.MAX_VALUE;
	int bidsHighestPrice 	= 0;
	
	private final BithumbAPI bithumbAPI;
	private final Currency currency;
	
	public APIPublicOrderBook(BithumbAPI bithumbAPI, Currency currency) {
		this.bithumbAPI 	= bithumbAPI;
		this.currency		= currency;
	}
	
	public List<OrderBookData> getAsksOrderBookData() { return asksOrderBook; }
	public List<OrderBookData> getBidsOrderBookData() { return bidsOrderBook; 	}
	public synchronized int getAsksLowestPrice() 	{ return asksLowestPrice; }			// 판매대기중 가장 낮은 금액
	public synchronized int getAsksHighestPrice() 	{ return asksHighestPrice; }		// 판매대기중 가장 높은 금액
	public synchronized int getBidsLowestPrice() 	{ return bidsLowestPrice; }			// 구매대기중 가장 낮은 금액
	public synchronized int getBidsHighestPrice() 	{ return bidsHighestPrice; }		// 구매대기중 가장 높은 금액
	
	public synchronized boolean update() {
		return parser( bithumbAPI.request( BithumbApiType.PUBLIC_ORDERBOOK, currency, null ));
	}

	private boolean parser(String strJsonResult ) {
		//System.out.println( strJsonResult );
		
		JSONObject jsonData;
		try {
			jsonData = ( JSONObject ) preParser( strJsonResult );
			if( jsonData == null ) {
				return false;
			}
			
			bidsParser( jsonData );		// 구매 요청
			asksParser( jsonData );		// 판매 요청
			
			return true;
			
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		
		return false;
	}

	
	
	private void asksParser(JSONObject jsonData) {
		JSONArray jsonArray = ( JSONArray ) jsonData.get("asks");
		
		while( asksOrderBook.size() < jsonArray.size()) {
			asksOrderBook.add( new OrderBookData( 0.0d, 0, false ));
		}
		
		asksLowestPrice 	= Integer.MAX_VALUE;
		asksHighestPrice 	= 0;
				
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonBid = ( JSONObject ) jsonArray.get(i);
			OrderBookData orderBookData = asksOrderBook.get(i);
			
			orderBookData.quantity  = Double.valueOf((String) jsonBid.get( "quantity" ));
			orderBookData.price 	= Util.priceStringToInteger( (String) jsonBid.get( "price" ));
			
			asksLowestPrice			= Math.min( asksLowestPrice, orderBookData.price );
			asksHighestPrice		= Math.max( asksHighestPrice, orderBookData.price );
		}
		
		Collections.sort( asksOrderBook );	
	}

	private void bidsParser(JSONObject jsonData) {
		JSONArray jsonArray = ( JSONArray ) jsonData.get("bids");
		
		while( bidsOrderBook.size() < jsonArray.size()) {
			bidsOrderBook.add( new OrderBookData( 0.0d, 0, true ));
		}
		
		bidsLowestPrice 	= Integer.MAX_VALUE;
		bidsHighestPrice 	= 0;
		
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonBid = ( JSONObject ) jsonArray.get(i);
			
			OrderBookData orderBookData = bidsOrderBook.get(i);
			
			orderBookData.quantity  = Double.valueOf((String) jsonBid.get( "quantity" ));
			orderBookData.price 	= Util.priceStringToInteger( (String) jsonBid.get( "price" ));
			
			bidsLowestPrice			= Math.min( bidsLowestPrice, orderBookData.price );
			bidsHighestPrice		= Math.max( bidsHighestPrice, orderBookData.price );
		}
		
		Collections.sort( bidsOrderBook );
	}

	private JSONObject preParser( String jsonString ) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = ( JSONObject ) parser.parse( jsonString );
	
		int status = Integer.valueOf(( String ) json.get("status"));
		if( status != 0 ) {
			String errMessage = ( String ) json.get("message");
			
			System.err.println( jsonString );
			System.err.println( errMessage );			
			return null;
		}
		
		JSONObject ret = ( JSONObject ) json.get( "data" );
		
		String order_currency = (String) ret.get("order_currency");
		if( order_currency.compareTo( currency.getSymbol()) != 0 ) {
			System.err.println( jsonString );
			System.err.println( String.format( "is not request order currency. order currency is '%s'", currency.getSymbol()) );
			return null;
		}
			
		return ret;
	}
	
	
	
	public class OrderBookData implements Comparable<OrderBookData> {
		public double quantity;
		public int price;
		public boolean isBids;
		public OrderBookData( double q, int p, boolean b ) {
			quantity = q;
			price = p;
			isBids = b;
		}
		
		public void print() {
			System.out.println( String.format( "[%s] %10s : %15f", isBids?"구매":"판매",Util.intergerToPriceString( price ), quantity ));
		}
		
		@Override
		public int compareTo(OrderBookData o) {
			return o.price - price;
		}
	};
	
	
	public void print() {
		System.out.println( String.format( "[%s OrderBook]", currency.getKorName()));
		for( OrderBookData data : asksOrderBook ) {
			data.print();
		}
		
		for( OrderBookData data : bidsOrderBook ) {
			data.print();
		}
	}
}

