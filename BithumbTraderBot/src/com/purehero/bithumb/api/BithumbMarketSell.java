package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY


https://api.bithumb.com/trade/market_sell			���尡 �Ǹ�

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
	status		��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	order_id	�ֹ� ��ȣ
	cont_id		ü�� ��ȣ
	units		�� �Ǹ� ����(������ ����)
	price		1Currency�� KRW �ü� (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	total		�Ǹ� KRW
	fee			�Ǹ� ������

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	units				Float			�ֹ� ����
		- 1ȸ �ּ� ���� (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.1 | ETC: 0.1 | XRP: 10 | BCH: 0.01 | XMR: 0.01)
		- 1ȸ �ŷ� �ѵ� : 1���
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC)


 */
public class BithumbMarketSell extends BithumbArrayBaseClass {
	double minUnits[] = { 
			0.001d, // BTC 
			0.01d,	// ETH
			0.01d,	// DASH
			0.1d,	// LTC
			0.1d,	// ETC
			10.0d,	// XRP
			0.01d,	// BCH
			0.01d	// XMR
	};
	
	private BithumbMyBalanceInfo balanceInfo = null;
	public boolean checkEnableOrder( int currency, BithumbMyBalanceInfo balanceInfo ) {
		this.setCurrency(currency);
	
		this.balanceInfo 	= balanceInfo;
		return balanceInfo.getBalances()[currency] >= minUnits[currency];
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
