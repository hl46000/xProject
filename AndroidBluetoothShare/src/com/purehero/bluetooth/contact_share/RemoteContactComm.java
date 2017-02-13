package com.purehero.bluetooth.contact_share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.common.G;
import com.purehero.common.Utils;

public class RemoteContactComm {
	final byte DEF_MAGIC_VALUE [] = { (byte)0x81, (byte)0x92, (byte)0x29, (byte)0x18 };
	
	private BluetoothCommunication btComm 	= null;
	
	public void setBluetoothCommunication(BluetoothCommunication btComm) {
		this.btComm = btComm;
		this.btComm.setEnableRequest( true );
	}

	public boolean isConnected() {
		if( btComm == null ) return false;
		return btComm.isConnected();
	}
	
	/**
	 * 수신된 데이터가 Command 용 데이터 인지를 확인한다. 
	 * 
	 * @param data
	 * @return
	 */
	public boolean isCommandData( byte [] data ) {
		byte temp_value [] = new byte[4];
		System.arraycopy( data, 0, temp_value, 0, temp_value.length );
		return Arrays.equals( temp_value, DEF_MAGIC_VALUE );
	}
	
	public synchronized void sendRequestRemoteDevice( byte op_code, byte [] data ) {
		if( btComm != null && btComm.isConnected() && btComm.isEnableRequest()) {
			G.Log( "requestRemoteDevice : 0x%x %dbytes", op_code, data == null ? 0 : data.length );
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
			try {
				outputStream.write( DEF_MAGIC_VALUE );
				
				byte op_code_bytes [] = { op_code };
				outputStream.write( op_code_bytes );
				
				byte data_size [] = Utils.intTobyte( data == null ? 0 : data.length );
				outputStream.write( data_size );
				
				if( data != null && data.length > 0 ) {
					outputStream.write( data );
				}
				
				btComm.write( outputStream.toByteArray() );
				btComm.flush();
				btComm.setEnableRequest( false );	// 요청을 전달 하였으면 응답이 올때까지 다른 요청은 전달되지 않게 막는다. 
				
			} catch( Exception e ) {
				e.printStackTrace();
				
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void sendResponseRemoteDevice( byte op_code,  byte [] data ) {
		if( btComm == null ) return;
		if( !btComm.isConnected()) return;
		
		G.Log( "sendResponseRemoteDevice : 0x%x %d bytes", op_code, data.length );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write( DEF_MAGIC_VALUE );
			
			byte op_code_bytes [] = { op_code };
			outputStream.write( op_code_bytes );
			
			byte data_size [] = Utils.intTobyte( data == null ? 0 : data.length );
			outputStream.write( data_size );
			
			if( data != null && data.length > 0 ) {
				outputStream.write( data );
			}
			
			btComm.write( outputStream.toByteArray(), true );
			btComm.flush();
			
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public void setEnableRequest(boolean b) {
		btComm.setEnableRequest(b);
	}
}
