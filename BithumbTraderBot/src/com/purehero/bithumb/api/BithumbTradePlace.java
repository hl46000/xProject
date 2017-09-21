package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY


https://api.bithumb.com/trade/place			bithumb 회원 판/구매 거래 주문 등록 및 체결
											(미수 주문등록 및 체결은 현 API에서 지원 안 함)

[Returned Example]
{
    "status"    : "0000",
    "order_id"  : "1428646963419",
    "data": [
        {
            "cont_id"   : "15313",
            "units"     : "0.61460000",
            "price"     : "284000",
            "total"     : 174546,
            "fee"       : "0.00061460"
        },
        {
            "cont_id"   : "15314",
            "units"     : "0.18540000",
            "price"     : "289000",
            "total"     : 53581,
            "fee"       : "0.00018540"
        }
    ]
}

[Returned Value Description]
	Key Name	Description
	status		결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	order_id	주문번호
	cont_id		체결번호
	units		체결 수량
	price		1Currency당 체결가 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	total		KRW 체결가
	fee			수수료

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	order_currency		String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC)
	Payment_currency	String			KRW (기본값)
	units				Float			주문 수량
		- 1회 최소 수량 (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.1 | ETC: 0.1 | XRP: 10 | BCH: 0.01 | XMR: 0.01)
		- 1회 최대 수량 (BTC: 300 | ETH: 2,500 | DASH: 4,000 | LTC: 15,000 | ETC: 30,000 | XRP: 2,500,000 | BCH: 1,200 | XMR: 10,000)
	price				Int				1Currency당 거래금액 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	type				String			거래유형 (bid : 구매, ask : 판매)
	misu				String			신용거래(Y : 사용, N : 일반) – 추후 제공


 */
public class BithumbTradePlace extends BithumbArrayBaseClass {
	@Override
	protected String getApiUri() {
		return "/trade/place";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		if( OrderType.NONE == orderType ) return null;
		
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("order_currency", 		CURRENCY_DEF.strCurrencies[currency] );
		rgParams.put("Payment_currency", 	"KRW" );
		rgParams.put("type", 	orderType == OrderType.SELL ? "ask" : "bid" );
		rgParams.put("units", 	CurrencyUtil.getDoubleToSellFormatString( orderUnits ));
		rgParams.put("price", 	String.valueOf( orderPrice ));
		
		return rgParams;
	}
	
	double orderUnits = 0.0d;					// 주문 개수
	public void setOrderUnits( double units ) {
		orderUnits = units;
	}
	
	OrderType orderType = OrderType.NONE; 	// 주문 종료( 구매, 판매 )
	public void setOrderType( OrderType type ) {
		orderType = type;
	}
	
	int orderPrice = 0;							// 주문 가격
	public void setOrderPrice( int price ) {
		orderPrice = price;
	}
	

	@Override
	protected void parser(JSONArray jsonArray) {
	}
}
