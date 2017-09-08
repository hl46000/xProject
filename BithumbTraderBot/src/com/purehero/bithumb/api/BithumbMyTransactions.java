package com.purehero.bithumb.api;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.purehero.bithumb.util.CURRENCY_DEF;


/**
 * bithumb ȸ�� ��/���� ü�� ����
 *
 * https://api.bithumb.com/info/user_transactions	ȸ�� �ŷ� ����
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
	status				��� ���� �ڵ� (���� : 0000, �����̿� �ڵ�� ���� �ڵ� ����)
	search				�˻� ���� (0 : ��ü, 1 : ���ſϷ�, 2 : �ǸſϷ�, 3 : �����, 4 : �Ա�, 5 : ���, 9 : KRW�Ա���)
	transfer_date		�ŷ� �Ͻ� Timestamp
	units				�ŷ� Currency ���� (BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR)
	price				�ŷ��ݾ�{currency}1krw	1Currency�� �ŷ��ݾ� (btc, eth, dash, ltc, etc, xrp, bch, xmr)
	fee					�ŷ�������
	{currency}_remain	�ŷ� �� Currency �ܾ� (btc, eth, dash, ltc, etc, xrp, bch, xmr)
	krw_remain			�ŷ� �� KRW �ܾ�

[Request Parameters]
	Parameter Name		Data Type		Description
	apiKey				String			apiKey
	secretKey			String			scretKey
	offset				Int				Value : 0 ~ (default : 0)
	count				Int				Value : 1 ~ 50 (default : 20)
	searchGb			String			0 : ��ü, 1 : ���ſϷ�, 2 : �ǸſϷ�, 3 : �����, 4 : �Ա�, 5 : ���, 9 : KRW�Ա���
	currency			String			BTC, ETH, DASH, LTC, ETC, XRP, BCH, XMR (�⺻��: BTC)
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
		rgParams.put("searchGb", String.valueOf(0));
		rgParams.put("currency", CURRENCY_DEF.strCurrencies[currency] );
		
		return rgParams;
	}

	@Override
	protected void parser(JSONArray jsonArray) {
		lastBuyPrice = 0;
		lastSellPrice = 0;
		
		for( int i = 0; i < jsonArray.size(); i++ ) {
			JSONObject jsonObject = ( JSONObject ) jsonArray.get(i);
			
			int serarch = Integer.valueOf((String)jsonObject.get("search"));
			if( serarch == 1 ) 			{ 	// ���� �Ϸ�
				lastBuyPrice = Integer.valueOf((String) jsonObject.get( CURRENCY_DEF.strCurrencies[currency].toLowerCase() + "1krw" ));
			} else if( serarch == 2 ) 	{	// �Ǹ� �Ϸ�
				lastSellPrice = Integer.valueOf((String) jsonObject.get( CURRENCY_DEF.strCurrencies[currency].toLowerCase() + "1krw" ));
			}
			
		}
		
		/*
		for( int i = 0; i < jsonArray.size(); i++ ) {
			break;
		}
		*/
	}

	int lastBuyPrice = 0;
	public int getLastBuyPrice() {
		return lastBuyPrice;
	}
	
	int lastSellPrice = 0;
	public int getLastSellPrice() {
		return lastSellPrice;
	}
}
