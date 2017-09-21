package com.purehero.bithumb.api;

import com.purehero.bithumb.util.CurrencyUtil;

public class OrderData implements Comparable<OrderData>{
	final OrderType type;
	final double quantity;
	final int price;
	
	public OrderData( OrderType t, double q, int p ) {
		type		= t;
		quantity 	= q;
		price 		= p;
	}
	
	public OrderType getType()			{ return type; }
	public String getTypeString()	{ return type == OrderType.BUY ? "구매" : "판매"; }
	public double getQuantity() 	{ return quantity; }
	public String getQuantityString() {
		return CurrencyUtil.getUnitsToFormatString( quantity );
	}
	public int getPrice() 			{ return price; }
	public String getPriceFormatString() {
		return CurrencyUtil.getIntegerToFormatString( price );		
	}

	@Override
	public int compareTo(OrderData o) {
		return o.getPrice() - this.getPrice();
	}
}
