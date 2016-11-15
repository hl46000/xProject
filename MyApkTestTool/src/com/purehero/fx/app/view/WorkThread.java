package com.purehero.fx.app.view;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IShellOutputReceiver;
import com.purehero.android.DeviceInfo;

public class WorkThread implements Runnable {
	final DeviceInfo deviceInfo;
	final String cmd;
	final IShellOutputReceiver receiver;
	public WorkThread( DeviceInfo deviceInfo, String cmd, IShellOutputReceiver receiver ) {
		this.deviceInfo = deviceInfo;
		this.cmd = cmd;
		this.receiver = receiver;
	}
	
	@Override
	public void run() {
		try {
			deviceInfo.getInterface().executeShellCommand( cmd, receiver );
		} catch (AdbCommandRejectedException e ) {
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return String.format( "[%d] %s", Thread.currentThread().getId(), cmd );
	}
}
