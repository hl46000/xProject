package com.purehero.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.purehero.contact.share.G;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothCommunication {
	BluetoothSocket mSocket			= null;
	InputStream		mInputStream 	= null;
	OutputStream	mOutputStream 	= null;
	
	private IFBluetoothDataReceiver receiver = null;
	
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
	}

	public void setDataReceiver( IFBluetoothDataReceiver receiver ) {
		this.receiver = receiver;
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
	}
	
	public String getName() {
		BluetoothDevice device = mSocket.getRemoteDevice();
		if( device != null ) {
			return device.getName() + "\n" + device.getAddress();
		}
		return "";
	}
	
	public void write( byte [] buff, int size ) {
		if( mOutputStream != null ) {
			try {
				mOutputStream.write( buff, 0, size );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
					if( receiver != null ) {
						receiver.OnDateReceived(data, readBytes);
					}
				} catch ( Exception e ) {
					break;
				}
			}
			
			G.Log( "'%s' End data receive", getName());
		}
	};
}
