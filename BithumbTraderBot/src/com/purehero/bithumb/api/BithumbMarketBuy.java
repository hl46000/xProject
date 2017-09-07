package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY


https://api.bithumb.com/trade/market_buy			시장가 구매

[Returned Example]
{
    "status"    : "0000",
    "order_id"  : "1429500241523",
    "data": [
        {
            "cont_id"   : "15364",
            "units"     : "0.16789964",
            "price"     : "270000",
            "total"     : 45333,
            "fee"       : "0.00016790"
        },
        {
            "cont_id"   : "15365",
            "units"     : "0.08210036",
            "price"     : "289000",
            "total"     : 23727,
            "fee"       : "0.00008210"
        }
    ]
}

[Returned Value Description]
	Key Name	Description
	status		결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	order_id	주문 번호
	cont_id		체결 번호
	units		총 구매 수량(수수료 포함)
	price		1Currency당 KRW 시세 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	total		구매 KRW
	fee			구매 수수료

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	units				Float			주문 수량
		- 1회 최소 수량 (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.1 | ETC: 0.1 | XRP: 10 | BCH: 0.01 | XMR: 0.01)
		- 1회 거래 한도 : 1억원
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC)


 */
public class BithumbMarketBuy extends BithumbArrayBaseClass {
	// 마지막 거래금액( 시장가격  )
	private BithumbLastTicker lastTicker = null;
	private BithumbMyBalanceInfo balanceInfo = null;
	public boolean checkEnableOrder( int currency, BithumbLastTicker lastTicker, BithumbMyBalanceInfo balanceInfo ) {
		this.setCurrency(currency);
		
		this.lastTicker 	= lastTicker;
		this.balanceInfo 	= balanceInfo;
		
		double units = balanceInfo.getKrw() / lastTicker.getLastMinSellPrice()[currency];
		return units >= CURRENCY_DEF.minUnits[currency];
	}
	
	@Override
	protected String getApiUri() {
		return "/trade/market_buy";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		double cache = (double)( balanceInfo.getKrw() * 0.15d );
		Double units = CurrencyUtil.getCurrencyUnits( getCurrency(), (double)( cache / lastTicker.getLastMinSellPrice()[currency]));

		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("currency", CURRENCY_DEF.strCurrencies[currency] );
		rgParams.put("units", units.toString() );
		
		return rgParams;
	}
	
	@Override
	protected void parser(JSONArray jsonArray) {
	}
}
