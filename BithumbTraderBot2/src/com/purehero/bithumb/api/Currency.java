package com.purehero.bithumb.api;

public enum Currency {
	BTC( "BTC", "비트코인","Bitcoin"), 
	ETH( "ETH", "이더리움","Ethereum"),
	DASH("DASH","대시","Dash"),
	LTC( "LTC", "라이트코인","Litecoin"),
	ETC( "ETC", "이더리움 클래식","Ethereum Classic"), 
	XRP( "XRP", "리플", "Ripple"),
	BCH( "BCH", "비트코인 캐시","Bitcoin Cash"), 
	XMR( "XMR", "모네로","Monero"),
	
	ALL( "ALL", "ALL", "ALL");
	
	private String symbol;
	private String koName;
	private String name;
	Currency(String symbol, String koName, String name ) {
        this.symbol 	= symbol;
        this.koName 	= koName;
        this.name		= name;
    }
 
    public String getSymbol() 	{ return symbol; }
    public String getName() 	{ return name; }
    public String getKorName() { return koName; }
}
