package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY


https://api.bithumb.com/trade/market_sell			시장가 판매

[Returned Example]
{
	"status"    : "0000",
	"order_id"  : "1429500318982",
    "data"      : [
        {
            "cont_id"   : "15366",
            "units"     : "0.78230769",
            "price"     : "260000",
            "total"     : 203400,
            "fee"       : 203
        },
        {
            "cont_id"   : "15367",
            "units"     : "0.21769231",
            "price"     : "259500",
            "total"     : 56491,
            "fee"       : 56
        }
    ]
}

[Returned Value Description]
	Key Name	Description
	status		결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	order_id	주문 번호
	cont_id		체결 번호
	units		총 판매 수량(수수료 포함)
	price		1Currency당 KRW 시세 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	total		판매 KRW
	fee			판매 수수료

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	units				Float			주문 수량
		- 1회 최소 수량 (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.1 | ETC: 0.1 | XRP: 10 | BCH: 0.01 | XMR: 0.01)
		- 1회 거래 한도 : 1억원
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC)


 */
public class BithumbMarketSell extends BithumbArrayBaseClass {
	private BithumbMyBalanceInfo balanceInfo = null;
	public boolean checkEnableOrder( int currency, BithumbMyBalanceInfo balanceInfo ) {
		this.setCurrency(currency);
	
		this.balanceInfo 	= balanceInfo;
		return balanceInfo.getBalances()[currency] >= CURRENCY_DEF.minUnits[currency];
	}
	
	@Override
	protected String getApiUri() {
		return "/trade/market_sell";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("currency", CURRENCY_DEF.strCurrencies[currency] );
		rgParams.put("units", CurrencyUtil.getDoubleToSellFormatString( balanceInfo.getBalances()[currency] ));
		
		return rgParams;
	}
	
	@Override
	protected void parser(JSONArray jsonArray) {
	}
}
