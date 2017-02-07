package com.purehero.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
	
	public static InputStream byteArrayToInputStream(byte[] srcBytes, int offset, int length ) {
	    return new ByteArrayInputStream(srcBytes, offset, length );
	}
	
	/**
     * byte 배열을 구분자/공백 없는 16진수 문자열로 변환
     * 
     * @param array
     * @return 16진수 스트링
     */
    public static String byteArrayToHexString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (array[i] & 0x00FF)).substring(1).toUpperCase());
            if ((i+1) % 16 == 0 && i != 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
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
	
	/**
	 * int형을 byte배열로 바꿈<br>
	 * @param integer
	 * @param order
	 * @return
	 */
	public static byte[] intTobyte(int integer ) {
 		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(ByteOrder.LITTLE_ENDIAN);
 
		// 인수로 넘어온 integer을 putInt로설정
		buff.putInt(integer);
		return buff.array();
	}
	
	public static byte[] longTobyte( long l ) {
 		ByteBuffer buff = ByteBuffer.allocate(Long.SIZE/8);
		buff.order(ByteOrder.LITTLE_ENDIAN);
 
		// 인수로 넘어온 integer을 putInt로설정
		buff.putLong(l);
		return buff.array();
	}
 
	/**
	 * byte배열을 int형로 바꿈<br>
	 * @param bytes
	 * @param order
	 * @return
	 */
	public static int byteToInt( byte[] bytes ) {
 		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(ByteOrder.LITTLE_ENDIAN);
 
		// buff사이즈는 4인 상태임
		// bytes를 put하면 position과 limit는 같은 위치가 됨.
		buff.put(bytes);
		// flip()가 실행 되면 position은 0에 위치 하게 됨.
		buff.flip();
		return buff.getInt(); // position위치(0)에서 부터 4바이트를 int로 변경하여 반환
	}
	
	public static long byteToLong( byte[] bytes ) {
 		ByteBuffer buff = ByteBuffer.allocate(Long.SIZE/8);
		buff.order(ByteOrder.LITTLE_ENDIAN);
 
		// buff사이즈는 4인 상태임
		// bytes를 put하면 position과 limit는 같은 위치가 됨.
		buff.put(bytes);
		// flip()가 실행 되면 position은 0에 위치 하게 됨.
		buff.flip();
		return buff.getLong(); // position위치(0)에서 부터 8바이트를 long로 변경하여 반환
	}
}
