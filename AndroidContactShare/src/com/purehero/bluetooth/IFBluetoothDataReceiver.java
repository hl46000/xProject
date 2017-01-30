package com.purehero.bluetooth;

public interface IFBluetoothDataReceiver {
	public void OnDateReceived( byte [] data, int size );
	public void OnDisconnected();
}
