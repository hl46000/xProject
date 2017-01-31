package com.purehero.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;

import com.purehero.bluetooth.share.G;

public class BluetoothManager {
	// Unique UUID for this application
    //private static final UUID MY_UUID_SECURE 	= UUID.fromString("0c785f08-e829-4ecc-adb5-74a0c8155a96");
    //private static final UUID MY_UUID_INSECURE 	= UUID.fromString("411ac2af-1baa-4b11-bbe2-74a0c8155a96");
	private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
	
	public static final int REQUEST_ENABLE_BT 				= 1023;
	public static final int REQUEST_CONNECT_DEVICE 			= 1024;
	public static final int REQUEST_DISCOVERABLE_DEVICE 	= 1025;
	
	private BluetoothAdapter btAdapter 	= null;
	private Activity activity = null;
	private Set<BluetoothDevice> bluetoothDevices = new HashSet<BluetoothDevice>();
	
	BluetoothCommunication 	btComm = null;
	SocketListenServer  	socketListenServer 			= null;
	SocketListenServer  	socketListenInsecureServer 	= null;
	BluetoothServerSocket 	mServerSocket 			= null;
	BluetoothServerSocket 		mInsecureServerSocket 	= null;
	IFBluetoothEventListener 	bluetoothEventListener 	= null;
	
	private static BluetoothManager instance = null;
	public static BluetoothManager getInstance() {
		if( instance == null ) {
			instance = new BluetoothManager();
		}
		return instance;
	}
	
	public void initialize ( Activity activity ) {
		this.activity = activity;
		btAdapter = BluetoothAdapter.getDefaultAdapter();		
	}
	
	public void release() {
		stopReceiveClient();
		
		if( btComm != null ) {
			btComm.release();
		}
	}
	
	public void addDevice( BluetoothDevice device ) {
		bluetoothDevices.add( device );
	}
	
	/**
	 * 다른 장치에서 내 장치를 찾을 수 있도록 duration 초동안 노출 시켜 준다. 
	 * 
	 * @param duration
	 */
	public void discoverableDevice( int duration ) {
		G.Log("discoverableDevice");
		
		// 감지 권한을 요구하는 인텐트 작성
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
		activity.startActivityForResult( discoverableIntent, REQUEST_DISCOVERABLE_DEVICE );
	}
	
	/**
	 * Bluetooth 장치가 사용 가능한지를 반환한다. 
	 * 
	 * @return true: 사용가능, false: 사용불가
	 */
	public boolean enableDevice() { 
		return btAdapter != null;  
	}

	/**
	 * Bluetooth 장치를 사용가능하도록 설정창을 띄워 준다. 
	 */
	public void requestEnable() { 
		if( !btAdapter.isEnabled()) {	
			Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
			activity.startActivityForResult(i, REQUEST_ENABLE_BT); 
		}
	}
	
	public void openDeviceList( Activity act ) { 
		G.Log("openDeviceList");
		
		Intent serverIntent = new Intent( act, BluetoothDeviceListActivity.class); 
		act.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); 
	}

	public boolean createBond(BluetoothDevice device) throws Exception { 
		if( device.getBondState() == BluetoothDevice.BOND_BONDED ) return true;
				
		Class<?> class1 = Class.forName("android.bluetooth.BluetoothDevice");
		Method createBondMethod = class1.getMethod("createBond");  
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);  
		return returnValue.booleanValue();  
	}  
	
	
	/**
	 * 입력된 장치에 연결을 시도 하고 연결이 성공하면 통신용 객체를 반환한다. 
	 * <br> 연결에 성공하면 Bond 리스트에 추가 시킨다. 
	 * 
	 * @param btDevice
	 * @return
	 * @throws IOException
	 */
	public BluetoothCommunication connectDevice( BluetoothDevice btDevice, boolean secure ) throws IOException {
		if( secure ) {
			btComm = new BluetoothCommunication( btDevice.createRfcommSocketToServiceRecord( MY_UUID_SECURE ));
		} else {
			btComm = new BluetoothCommunication( btDevice.createInsecureRfcommSocketToServiceRecord( MY_UUID_INSECURE ));
		}
		if( btComm != null ) {
			try {
				createBond( btDevice );
			} catch( Exception e ) {}
		}
		
		if( bluetoothEventListener != null ) {
			bluetoothEventListener.OnConnected( btComm );
		}
		activity.finish();
		
		return btComm;
	}
	
	IFBluetoothEventListener btEventListener = new IFBluetoothEventListener() {
		@Override
		public void OnDateReceived(byte[] data, int size) {}

		@Override
		public void OnConnected(BluetoothCommunication newComm ) {
			G.Log( "receivedConnection" );
			if( btComm != null && btComm.isConnected()) { 
				newComm.release();
				return;
			} 
			btComm = newComm;
			G.confirmDialog( activity, "연결요청 확인", btComm.getName() + " 으로부터 연청 요청을 받았습니다.\n연결하시겠습니까?", 0, dialogOnClickListener );
		}

		@Override
		public void OnDisconnected() {}
	};
	
	public void SetBluetoothEventListener( IFBluetoothEventListener bluetoothEventListener ) {
		this.bluetoothEventListener = bluetoothEventListener;
	}
	
	DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int id ) {
			switch( id ) {
			case G.DIALOG_BUTTON_ID_YES :
				G.Log( "Dialog onClickListener : YES" );
				btComm.setEventListener( bluetoothEventListener );
				
				if( bluetoothEventListener != null ) {
					bluetoothEventListener.OnConnected( btComm );
				}
				activity.finish();
				
				break;
			case G.DIALOG_BUTTON_ID_NO 	:
				G.Log( "Dialog onClickListener : NO" );
				btComm.release();
				break;
			}
		}
	};
	
	/**
	 * 현 장치에 새로운 연결이 들어오기를 기다린다.
	 * <br>새로운 장치와 연결이 되면 BluetoothConnection interface 을 통해 연결된 객체를 전달하고 
	 * 다음 연결이 있을 때 까지 대기 한다.  
	 * 
	 * @param serverName
	 * @param bc
	 * @throws IOException
	 */
	public void startReceiveClient() throws IOException {
		G.Log( "startReceiveClient" );
		stopReceiveClient();
		
		String serverName = "BluetoothShare";
		
		mServerSocket = btAdapter.listenUsingRfcommWithServiceRecord( serverName, MY_UUID_SECURE );
		if( mServerSocket != null ) {
			socketListenServer = new SocketListenServer( mServerSocket, btEventListener );
			socketListenServer.start();
		}
		mInsecureServerSocket = btAdapter.listenUsingInsecureRfcommWithServiceRecord( serverName, MY_UUID_INSECURE );
		if( mInsecureServerSocket != null ) {
			socketListenInsecureServer = new SocketListenServer( mInsecureServerSocket, btEventListener );
			socketListenInsecureServer.start();
		}
	}

	/**
	 * 새로운 장치의 연결을 기다리는 작업을 정지 시킨다. 
	 */
	public void stopReceiveClient() {
		G.Log( "stopReceiveClient" );
		
		if( mServerSocket != null ) {
			try { mServerSocket.close(); } catch (IOException e1) {}
		}
		if( socketListenServer != null ) {
			try { socketListenServer.join(); } catch (InterruptedException e) {}
		}
		mServerSocket = null;
		socketListenServer = null;
		
		if( mInsecureServerSocket != null ) {
			try { mInsecureServerSocket.close(); } catch (IOException e1) {}
		}
		if( socketListenInsecureServer != null ) {
			try { socketListenInsecureServer.join(); } catch (InterruptedException e) {}
		}
		
		mInsecureServerSocket	= null;
		mInsecureServerSocket	= null;
	}
	
	class SocketListenServer extends Thread implements Runnable{
		final IFBluetoothEventListener listener;
		final BluetoothServerSocket ss;
		public SocketListenServer( BluetoothServerSocket serverSocket, IFBluetoothEventListener listener ) {
			this.listener = listener;
			this.ss = serverSocket;
		}
		
		@Override
		public void run() {
			G.Log( "Bluetooth server socket %d started", Thread.currentThread().getId() );
			
			while ( true ) {
				try {
					try { Thread.sleep( 300 ); } catch( Exception e){}
					
					BluetoothSocket bs = ss.accept();
					if( bs == null ) {
						G.Log( "Timeout accept" );
						continue;
					}
					
					BluetoothDevice bd = bs.getRemoteDevice();
					G.Log( "Accepted : %s(%s)", bd.getName(), bd.getAddress() );
					
					BluetoothCommunication newConnection = new BluetoothCommunication( bs ); 
					if( listener != null ) {
						listener.OnConnected( newConnection );
					} else {
						bs.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			
			G.Log( "Bluetooth server socket %d stopped", Thread.currentThread().getId() );
		}
		
	};
		
	/**
	 * @param address
	 * @return
	 */
	public BluetoothDevice getDevice(String address) {
		return btAdapter.getRemoteDevice( address );
	}

	public void write(byte[] msg_bytes, int length) {
		if( btComm != null ) {
			btComm.write( msg_bytes, length );
		}
	}
}
