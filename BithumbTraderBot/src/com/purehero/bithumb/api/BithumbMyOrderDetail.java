package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


/**
 * bithumb 회원 판/구매 체결 내역
 *
 * https://api.bithumb.com/info/order_detail		bithumb 회원 판/구매 체결 내역
 * 
 * @author MY
 *
 * 
[Returned Example]
{
    "status"    : "0000",
    "data"      : [
        {
            "transaction_date"  : "1428024598967",
            "type"              : "ask",
            "order_currency"    : "BTC",
            "payment_currency"  : "KRW",
            "units_traded"      : "0.0017",
            "price"             : "264000",
            "fee"               : "0.0000017",
            "total"             : "449"
        }
    ]
}

[Returned Value Description]
	Key Name			Description
	status				결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	transaction_date	채결 시간 Timestamp
	type				bid(구매), ask(판매)
	order_currency		BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR
	payment_currency	KRW
	units_traded		체결 수량
	price				1Currency당 체결가 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	fee					수수료
	total				체결가

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	order_id			String			판/구매 주문 등록된 주문번호
	type				String			거래유형 (bid : 구매, ask : 판매)
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC)
 *
 *
 */
public class BithumbMyOrderDetail extends BithumbBaseClass {
	public String toInfoString() {
		String ret = "";
		return ret;
	}

	@Override
	protected String getApiUri() {
		return "/info/order_detail";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("currency", CURRENCY_DEF.strCurrencies[currency] );
		
		return rgParams;
	}

	@Override
	protected void parser(JSONObject jsonData) {
		// TODO Auto-generated method stub
		
	}
}
