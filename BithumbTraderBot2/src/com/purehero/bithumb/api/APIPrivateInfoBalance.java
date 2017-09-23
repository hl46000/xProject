package com.purehero.bithumb.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APIPrivateInfoBalance {
	private double total_balances[] 		= new double[Currency.ALL.ordinal() + 1 ];		// 전체 금액
	private double available_balances[] 	= new double[Currency.ALL.ordinal() + 1 ];		// 사용가능한 금액
	private double in_use__balances[] 		= new double[Currency.ALL.ordinal() + 1 ];		// 사용중인 금액
	
	private int total_krw = 0;
	private int available_krw = 0;
	private int in_use_krw = 0;
	private BithumbAPI api;
	
	public APIPrivateInfoBalance( BithumbAPI api ) {
		this.api = api;
	}
	
	public boolean update() {
		return parser( api.request( BithumbApiType.PRIVATE_INFO_BALANCE, Currency.ALL, null ));
	}

	public int getTotalKrw() { return total_krw; }
	public int getAvailableKrw() { return available_krw; }
	public int getInUseKrw() { return in_use_krw; }
	
	public double getInUseBalance( Currency currency ) 	 { return in_use__balances[ currency.ordinal() ]; }
	public double getAvaliableBalance( Currency currency ) { return available_balances[ currency.ordinal() ]; }
	public double getTotalBalance( Currency currency ) 	 { return total_balances[ currency.ordinal() ]; }
	
	private boolean parser( String strJsonResult ) {
		try {
			JSONObject jsonData = ( JSONObject ) preParser( strJsonResult );
			if( jsonData == null ) {
				return false;
			}
			
			total_krw 		= ((Long) jsonData.get("total_krw")).intValue();
			available_krw 	= ((Long) jsonData.get("available_krw")).intValue();
			in_use_krw 		= ((Long) jsonData.get("in_use_krw")).intValue();
			
			for( Currency currency : Currency.values()) {
				Object objValue = jsonData.get( "total_" + currency.getSymbol().toLowerCase() );
				if( objValue != null ) {
					total_balances[currency.ordinal()] = Double.valueOf( (String) objValue );
				} else {
					total_balances[currency.ordinal()] = 0.0f;
				}
				
				objValue = jsonData.get( "available_" + currency.getSymbol().toLowerCase() );
				if( objValue != null ) {
					available_balances[currency.ordinal()] = Double.valueOf( (String) objValue );
				} else {
					available_balances[currency.ordinal()] = 0.0f;
				}
				
				objValue = jsonData.get( "in_use_" + currency.getSymbol().toLowerCase() );
				if( objValue != null ) {
					in_use__balances[currency.ordinal()] = Double.valueOf( (String) objValue );
				} else {
					in_use__balances[currency.ordinal()] = 0.0f;
				}
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private JSONObject preParser( String jsonString ) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = ( JSONObject ) parser.parse( jsonString );
	
		int status = Integer.valueOf(( String ) json.get("status"));
		if( status != 0 ) {
			String errMessage = ( String ) json.get("message");
			
			System.err.println( jsonString );
			System.err.println( errMessage );			
			return null;
		}
		
		return ( JSONObject ) json.get( "data" );
	}
	
	public void print() {
		System.out.printf( "[%16s] ", "KRW");
		System.out.printf( "TOTAL:%8d, ", 		total_krw );
		System.out.printf( "AVAILABLE:%8d, ",	available_krw );
		System.out.printf( "IN_USE:%8d",			in_use_krw );
		
		for( Currency currency : Currency.values()) {
			if( currency.getName().compareTo("ALL") == 0 ) continue;
			
			System.out.printf( "\n[%16s] ", currency.getName());
			System.out.printf( "TOTAL:%f, ", 		getTotalBalance( currency ));
			System.out.printf( "AVAILABLE:%f, ", 	getAvaliableBalance( currency ));
			System.out.printf( "IN_USE:%f", 		getInUseBalance( currency ));
		}
	}
}
