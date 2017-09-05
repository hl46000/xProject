package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


/**
 * bithumb 회원 판/구매 체결 내역
 *
 * https://api.bithumb.com/info/user_transactions	회원 거래 내역
 * 
 * @author MY
 *
 * 
[Returned Example]
{
    "status"    : "0000",
    "data"      : [
        {
            "search"        : "2",
            "transfer_date" : 1417139122544,
            "units"         : "- 0.1",
            "price"         : "51600",
            "btc1krw"       : "516000",
            "fee"           : "0 KRW",
            "btc_remain"    : "665.40127447",
            "krw_remain"    : "305507280"
        },
        {
            "search"        : "2",
            "transfer_date" : 1417138805912,
            "units"         : "- 0.1",
            "price"         : "51600",
            "btc1krw"       : "516000",
            "fee"           : "0 KRW",
            "btc_remain"    : "665.50127447",
            "krw_remain"    : "305455680"
        }
    ]
}

[Returned Value Description]
	Key Name			Description
	status				결과 상태 코드 (정상 : 0000, 정상이외 코드는 에러 코드 참조)
	search				검색 구분 (0 : 전체, 1 : 구매완료, 2 : 판매완료, 3 : 출금중, 4 : 입금, 5 : 출금, 9 : KRW입금중)
	transfer_date		거래 일시 Timestamp
	units				거래 Currency 수량 (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	price				거래금액{currency}1krw	1Currency당 거래금액 (btc, eth, dash, ltc, etc, xrp, bch, xmr)
	fee					거래수수료
	{currency}_remain	거래 후 Currency 잔액 (btc, eth, dash, ltc, etc, xrp, bch, xmr)
	krw_remain			거래 후 KRW 잔액

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	offset				Int				Value : 0 ~ (default : 0)
	count				Int				Value : 1 ~ 50 (default : 20)
	searchGb			String			0 : 전체, 1 : 구매완료, 2 : 판매완료, 3 : 출금중, 4 : 입금, 5 : 출금, 9 : KRW입금중
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (기본값: BTC)
 *
 *
 */
public class BithumbMyTransactions extends BithumbArrayBaseClass {
	public String toInfoString() {
		String ret = "";
		return ret;
	}

	@Override
	protected String getApiUri() {
		return "/info/user_transactions";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("offset", String.valueOf(0));
		rgParams.put("searchGb", String.valueOf(1));
		rgParams.put("currency", CURRENCY_DEF.strCurrencies[currency] );
		
		return rgParams;
	}

	@Override
	protected void parser(JSONArray jsonArray) {
		lastUnitPrice = 0;
		
		JSONObject jsonObject = ( JSONObject ) jsonArray.get(0);
		lastUnitPrice = Integer.valueOf((String) jsonObject.get( CURRENCY_DEF.strCurrencies[currency].toLowerCase() + "1krw" ));
		
		/*
		for( int i = 0; i < jsonArray.size(); i++ ) {
			break;
		}
		*/
	}

	int lastUnitPrice = 0;
	public int getLastUnitPrice() {
		return lastUnitPrice;
	}
}
