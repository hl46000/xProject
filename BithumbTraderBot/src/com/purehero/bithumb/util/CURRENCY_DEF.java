package com.purehero.bithumb.util;

public class CURRENCY_DEF {
	public static final int BTC 			= 0;
	public static final int ETH				= 1;
	public static final int DASH			= 2;
	public static final int LTC				= 3;
	public static final int ETC				= 4;
	public static final int XRP				= 5;
	public static final int BCH				= 6;
	public static final int XMR				= 7;

	public static final int ALL				= Currency.ALL.ordinal();
	public static final int MAX_CURRENCY	= ALL;
	
	public static final String strCurrencies[] = { "BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "XMR" };
	public static final String strCurrenciesKOR[] = { "비트코인", "이더리움", "대쉬", "라이트코인", "이더리움 클래식", "리플", "비트코인 캐쉬", "모네로" };
	
	public static final double minUnits[] = { 
			0.001d, // BTC 
			0.01d,	// ETH
			0.01d,	// DASH
			0.1d,	// LTC
			0.1d,	// ETC
			10.0d,	// XRP
			0.01d,	// BCH
			0.01d	// XMR
	};
}
