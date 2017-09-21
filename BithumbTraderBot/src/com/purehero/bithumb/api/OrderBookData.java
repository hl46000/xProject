package com.purehero.bithumb.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CurrencyUtil;

public class OrderBookData {
	List<OrderData> orderDatas = new ArrayList<OrderData>();	
	
	int highestPrice[] 	= { 0, 0 };
	int lowestPrice[] 	= { 0, 0 };
	
	public void bids(JSONArray jsonArray) {
		loadOrderDatas( OrderType.BUY, orderDatas, jsonArray );
	}

	public void asks(JSONArray jsonArray) {
		loadOrderDatas( OrderType.SELL, orderDatas, jsonArray );
	}
	
	private void loadOrderDatas( OrderType type, List<OrderData> orderDatas, JSONArray jsonArray) {
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonBid = ( JSONObject ) jsonArray.get(i);
			
			double quantity = Double.valueOf((String) jsonBid.get( "quantity" ));
			int price 		= CurrencyUtil.priceStringToInteger( (String) jsonBid.get( "price" ));
			
			highestPrice[type.ordinal()] 	= Math.max( highestPrice[type.ordinal()], price );
			lowestPrice[type.ordinal()]		= Math.min( lowestPrice[type.ordinal()], price );
			
			orderDatas.add( new OrderData( type, quantity, price ));
		}
		
		Collections.sort( orderDatas );		
	}

	public int getHighestPrice( OrderType type ) 	{ return highestPrice[type.ordinal()]; }
	public int getLowestPrice( OrderType type)		{ return lowestPrice[type.ordinal()]; }
	
	public List<OrderData> getOrderDatas() {
		return orderDatas;
	}
		
	public void clear() {		
		orderDatas.clear();		
	}
}
