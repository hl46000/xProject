package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


/**
 * bithumb ȸ�� ��/���� ü�� ����
 *
 * https://api.bithumb.com/info/order_detail		bithumb ȸ�� ��/���� ü�� ����
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
	status				��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	transaction_date	ä�� �ð� Timestamp
	type				bid(����), ask(�Ǹ�)
	order_currency		BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR
	payment_currency	KRW
	units_traded		ü�� ����
	price				1Currency�� ü�ᰡ (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	fee					������
	total				ü�ᰡ

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	order_id			String			��/���� �ֹ� ��ϵ� �ֹ���ȣ
	type				String			�ŷ����� (bid : ����, ask : �Ǹ�)
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC)
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
