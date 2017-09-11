package com.purehero.bithumb.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CurrencyUtil;

public class OrderBookData {
	List<OrderData> orderDatas = new ArrayList<OrderData>();	
	
	public void bids(JSONArray jsonArray) {
		loadOrderDatas( 0, orderDatas, jsonArray );
	}

	public void asks(JSONArray jsonArray) {
		loadOrderDatas( 1, orderDatas, jsonArray );
	}
	
	private void loadOrderDatas( int type, List<OrderData> orderDatas, JSONArray jsonArray) {
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonBid = ( JSONObject ) jsonArray.get(i);
			
			double quantity = Double.valueOf((String) jsonBid.get( "quantity" ));
			int price 		= CurrencyUtil.priceStringToInteger( (String) jsonBid.get( "price" ));
			
			orderDatas.add( new OrderData( type, quantity, price ));
		}
		
		Collections.sort( orderDatas );		
	}

	public List<OrderData> getOrderDatas() {
		return orderDatas;
	}
		
	public void clear() {		
		orderDatas.clear();		
	}
}
