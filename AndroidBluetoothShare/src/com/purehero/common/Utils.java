package com.purehero.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utils {
	public static byte[] inputStreamToByteArray(InputStream is) {
	    byte[] resBytes = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	     
	    byte[] buffer = new byte[1024];
	    int read = -1;
	    try {
	        while ( (read = is.read(buffer)) != -1 ) {
	           bos.write(buffer, 0, read);
	        }
	         
	        resBytes = bos.toByteArray();
	        bos.close();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	     
	    return resBytes;
	}
	
	public static InputStream byteArrayToInputStream(byte[] srcBytes) {
	    return new ByteArrayInputStream(srcBytes);
	}
	
	public static byte[] compress(byte[] data) throws IOException {  
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
		deflater.finish();  
		byte[] buffer = new byte[1024];   
		
		while (!deflater.finished()) {  
			int count = deflater.deflate(buffer); // returns the generated code... index  
			outputStream.write(buffer, 0, count);   
		}  
		outputStream.close();  
		   
		byte[] output = outputStream.toByteArray();  
		G.Log("Original: " + data.length + " byte");  
		G.Log("Compressed: " + output.length + " byte");
		
		return output;  
	}  
	
	public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
		Inflater inflater = new Inflater();   
		inflater.setInput(data);  
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
		byte[] buffer = new byte[1024];  
		while (!inflater.finished()) {  
			int count = inflater.inflate(buffer);  
			outputStream.write(buffer, 0, count);  
		}  
		outputStream.close();
		
		byte[] output = outputStream.toByteArray();  
		G.Log("Compressed: " + data.length+ " byte");  
		G.Log("Decompressed: " + output.length+ " byte");  
		   
		return output;  
	}  
}
