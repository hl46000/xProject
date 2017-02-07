package com.purehero.bluetooth;

public interface IFBluetoothEventListener {
	public void OnDataReceived( byte [] data, int size );
	public void OnConnected( final BluetoothCommunication btComm);
	public void OnDisconnected();
}
