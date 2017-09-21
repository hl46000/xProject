package com.purehero.bithumb.api;


public class APIKey {
	private static final String encrypted_api_key 		= "g6gbTFcRbKOY+psodm6SiCL1qXbkzHGgzcCZr8iGwWU6AKOB0Bt4/r2CYtztxkb8";
	private static final String encrypted_secure_key 	= "TyE7JvFj+idevYeGgZOZ2yzcSdMSHo+S5pg5TZeS2j0mjGHjeG+0duhZFDFZZ8pD";
	
	public static String getAPIKey() 				{ 
		try {
			return AES.decAES( encrypted_api_key );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getSecureKey() 			{ 
		try {
			return AES.decAES( encrypted_secure_key );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getEncryptedAPIKey() 		{ return encrypted_api_key; }
	public static String getEncryptedSecureKey() 	{ return encrypted_secure_key; }
}
