package com.purehero.bithumb.api;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CurrencyUtil;

public class OrderBookData {
	List<OrderData> bidsOrderDatas = new ArrayList<OrderData>();
	List<OrderData> asksOrderDatas = new ArrayList<OrderData>();
	
	public void bids(JSONArray jsonArray) {
		loadOrderDatas( bidsOrderDatas, jsonArray );
	}

	public void asks(JSONArray jsonArray) {
		loadOrderDatas( asksOrderDatas, jsonArray );
	}
	
	private void loadOrderDatas(List<OrderData> orderDatas, JSONArray jsonArray) {
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonBid = ( JSONObject ) jsonArray.get(i);
			
			double quantity = Double.valueOf((String) jsonBid.get( "quantity" ));
			int price 		= CurrencyUtil.priceStringToInteger( (String) jsonBid.get( "price" ));
			
			orderDatas.add( new OrderData( quantity, price ));
		}
	}

	class OrderData {
		final double quantity;
		final int price;
		
		public OrderData( double q, int p ) {
			quantity 	= q;
			price 		= p;
		}
		
		public double getQuantity() { return quantity; }
		public int getPrice() { return price; }
	}
}
