package com.purehero.bithumb.api;

public enum BithumbApiType {
	PUBLIC_TICKER("/public/ticker/"),				// bithumb 거래소 마지막 거래 정보
	PUBLIC_ORDERBOOK( "/public/orderbook/" ), 	// bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
	PUBLIC_RECENT_TRANSACTIONS("/public/recent_transactions/"),	// bithumb 거래소 거래 체결 완료 내역
	
	PRIVATE_INFO_ACCOUNT("/info/account" ),					// bithumb 거래소 회원 정보
	PRIVATE_INFO_BALANCE( "/info/balance" ),					// bithumb 거래소 회원 지갑 정보
	PRIVATE_INFO_WALLET_ADDRESS("/info/wallet_address" ),	// bithumb 거래소 회원 입금 주소
	PRIVATE_INFO_TICKER( "/info/ticker" ),					// 회원 마지막 거래 정보
	
	BITHUMB_API_END("");
	
	private String url;
	 
	BithumbApiType(String envUrl) {
        this.url = envUrl;
    }
 
    public String getUrl( Currency currency ) {
        if( url.startsWith("/public")) {
        	return url + currency.getSymbol();
        } 
        return url;
    }
}
