package com.purehero.bithumb.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	private static Key getAESKey() throws UnsupportedEncodingException {
	    String key = "1085a0c2-0bdf-4e8a-9afb-76582876afd4".substring(0, 16);

	    byte[] keyBytes = new byte[16];
	    byte[] b = key.getBytes("UTF-8");

	    int len = b.length;
	    if (len > keyBytes.length) {
	       len = keyBytes.length;
	    }

	    System.arraycopy(b, 0, keyBytes, 0, len);
	    return new SecretKeySpec(keyBytes, "AES");
	}

	// 암호화
	public static String encAES( String str ) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException  {
	    Key keySpec = getAESKey();
	    IvParameterSpec ivSpec = new IvParameterSpec(keySpec.getEncoded());
	    
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	    String enStr = new String(Base64.getEncoder().encode(encrypted));
	    
	    return enStr;
	}
	
	// 복호화
	public static String decAES(String enStr) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
	    Key keySpec = getAESKey();
	    IvParameterSpec ivSpec = new IvParameterSpec(keySpec.getEncoded());
	    
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, keySpec, ivSpec );
	    byte[] byteStr = Base64.getDecoder().decode( enStr.getBytes("UTF-8"));
	    String decStr = new String(c.doFinal(byteStr), "UTF-8");

	    return decStr;
	}
}
