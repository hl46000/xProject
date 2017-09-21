package com.purehero.bithumb.api;

public enum Currency {
	BTC( "BTC", "비트코인"), 
	ETH( "ETH", "이더리움"), 
	DASH("DASH","대시"), 
	LTC( "LTC", "라이트코인"), 
	ETC( "ETC", "이더리움 클래식"), 
	XRP( "XRP", "리플"), 
	BCH( "BCH", "비트코인 캐시"), 
	XMR( "XMR", "모네로"),
	
	ALL( "ALL", "전체");
	
	private String symbol;
	private String name;
	Currency(String symbol, String name ) {
        this.symbol = symbol;
    }
 
    public String getSymbol() {
        return symbol;
    }
    
    public String getName() {
        return name;
    }
}
