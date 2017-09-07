package com.purehero.bithumb.api;

import java.util.HashMap;

import org.json.simple.JSONArray;

import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


/**
 * @author MY


https://api.bithumb.com/trade/market_buy			���尡 ����

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
	status		��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	order_id	�ֹ� ��ȣ
	cont_id		ü�� ��ȣ
	units		�� ���� ����(������ ����)
	price		1Currency�� KRW �ü� (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	total		���� KRW
	fee			���� ������

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	units				Float			�ֹ� ����
		- 1ȸ �ּ� ���� (BTC: 0.001 | ETH: 0.01 | DASH: 0.01 | LTC: 0.1 | ETC: 0.1 | XRP: 10 | BCH: 0.01 | XMR: 0.01)
		- 1ȸ �ŷ� �ѵ� : 1���
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC)


 */
public class BithumbMarketBuy extends BithumbArrayBaseClass {
	// ������ �ŷ��ݾ�( ���尡��  )
	private BithumbLastTicker lastTicker = null;
	private BithumbMyBalanceInfo balanceInfo = null;
	public boolean checkEnableOrder( int currency, BithumbLastTicker lastTicker, BithumbMyBalanceInfo balanceInfo ) {
		this.setCurrency(currency);
		
		this.lastTicker 	= lastTicker;
		this.balanceInfo 	= balanceInfo;
		
		double units = balanceInfo.getKrw();
		units /= lastTicker.getLastMinSellPrice()[currency];
		
		return units >= CURRENCY_DEF.minUnits[currency];
	}
	
	@Override
	protected String getApiUri() {
		return "/trade/market_buy";
	}

	@Override
	protected HashMap<String, String> getApiRequestParams() {
		double cache = (double)balanceInfo.getKrw();
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
