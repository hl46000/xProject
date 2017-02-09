package com.purehero.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.purehero.common.G;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothCommunication {
	BluetoothSocket mSocket			= null;
	InputStream		mInputStream 	= null;
	OutputStream	mOutputStream 	= null;
	
	private IFBluetoothEventListener bluetoothEventListenerreceiver = null;
	private boolean isEnableRequest = true;
	
	public BluetoothCommunication( BluetoothSocket mSocket ) throws IOException {
	    this.mSocket = mSocket;
	    
	    if( mSocket == null ) {
	    	throw new IOException( "mSocket is null" );
	    }
	    if(! mSocket.isConnected()) {
	    	mSocket.connect();
	    }
	    
	    mOutputStream = mSocket.getOutputStream();
	    mInputStream = mSocket.getInputStream();
	    
	    isEnableRequest = true;
	    new DataReceiver().start();
	}

	public boolean isConnected() {
		return mSocket == null ? false : mSocket.isConnected();
	}
	
	public void release() {
		if( mOutputStream != null ) {
			try { mOutputStream.close(); } catch (IOException e) {}
		}
		
		if( mInputStream != null ) {
			try { mInputStream.close(); } catch (IOException e) {}
		}
		
		if( mSocket != null ) {
			try { mSocket.close(); } catch (IOException e) {}
		}
		
		if( bluetoothEventListenerreceiver != null ) {
			bluetoothEventListenerreceiver.OnDisconnected();
		}
	}
	
	public String getName() {
		BluetoothDevice device = mSocket.getRemoteDevice();
		if( device != null ) {
			//return device.getName() + "\n" + device.getAddress();
			return device.getName();
		}
		return "";
	}
	
	public void flush() {
		if( mOutputStream != null ) {
			try {
				mOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 연결된 socket 으로 데이터 전송 허용 여부를 설정한다. 
	 * 
	 * @param enable	전송 허용 여부, true : 허용, false : 차단
	 */
	public void setEnableRequest( boolean enable ) {
		isEnableRequest = enable;
	}
	public boolean isEnableRequest() {
		return isEnableRequest;
	}
	
	/**
	 * 연결된 Socket 으로 데이터를 기록한다. 
	 * 
	 * @param buff	socket으로 전송할 데이터
	 * @return		전송된 데이터의 byte 수, 오류 발생 시 -1반환, 전송이 허용되지 않은 경우는 0 반환
	 */
	public int write( byte [] buff ) {
		return write( buff, false );
	}
	
	public int write( byte [] buff, boolean force ) {
		if( mOutputStream != null ) {
			if( !isEnableRequest && !force ) return 0;
			try {
				mOutputStream.write( buff );
				return buff.length;
				
			} catch (IOException e) {
				e.printStackTrace();
				release();
			}
		}
		
		return -1;
	}
	
	class DataReceiver extends Thread implements Runnable {
		@Override
		public void run() {
			final int buffer_size = 1024 * 4;
			
			G.Log( "'%s' Start data receive", getName());
			byte data [] = new byte[ buffer_size ];
			int readBytes = 0;
			while( true ) {
				try {
					readBytes = mInputStream.read( data, 0, buffer_size );
					
					if( bluetoothEventListenerreceiver != null ) {
						bluetoothEventListenerreceiver.OnDataReceived(data, readBytes);
					}
				} catch ( Exception e ) {
					e.printStackTrace();
					break;
				}
			}
						
			G.Log( "'%s' End data receive", getName());
			
			release();
		}
	}

	public void setEventListener(IFBluetoothEventListener bluetoothEventListenerreceiver) {
		this.bluetoothEventListenerreceiver = bluetoothEventListenerreceiver;
	};
}
