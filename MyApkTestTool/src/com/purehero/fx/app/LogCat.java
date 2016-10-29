package com.purehero.fx.app;

import java.util.List;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.ddmlib.logcat.LogCatReceiverTask;

public class LogCat {
	private LogCatReceiverTask logcatTask;
	private final CircularList<String> logCatBuffer;
	
	protected final IDevice device;
	public LogCat( IDevice device ) {
		this.device = device;
		logCatBuffer = new CircularList<String>( 10000 );	// logcat 정보를 최대 10000줄 까지 저장한다. 
	}
	
	protected boolean bIsLogcatStarted = false;
	protected LogCatListener logCatListener = null;
	public boolean isLogcatStarted() { return bIsLogcatStarted; }
	/**
	 * LogCat 수집을 시작합니다. 
	 * @param listener 수집된 LogCat 메제지를 수신할 Interface 
	 */
	public void logCatStart( LogCatListener listener ) {
		logcatTask = new LogCatReceiverTask(device);
		logcatTask.addLogCatListener( internal_logcatListener );
		logcatTask.addLogCatListener( listener );
		logCatListener = listener;
		
		new Thread( new Runnable(){
			@Override
			public void run() {
				bIsLogcatStarted = true;
				logCatClear();
				logcatTask.run();
				bIsLogcatStarted = false;
			}}).start();
	}
	/**
	 * LogCat 수집을 정지 합니다. 
	 */
	public void logCatStop() {
		if( logCatListener != null ) {
			logcatTask.removeLogCatListener( logCatListener );
		}
		logcatTask.stop();
		logcatTask = null;
	}
	/**
	 * ADB Shell 명령어의 결과값을 받을 공통 리시버 입니다. 
	 */
	private IShellOutputReceiver LogCatOutputReceiver = new IShellOutputReceiver() {
		@Override
		public void addOutput(byte[] data, int offset, int length) {}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	/**
	 * LogCat 정보를 초기화 합니다. 
	 */
	public void logCatClear() {
		try {
			logCatBuffer.clear();
			device.executeShellCommand("logcat -c", LogCatOutputReceiver );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	LogCatListener internal_logcatListener = new LogCatListener() {
		@Override
		public void log(List<LogCatMessage> msgList) {
			for( LogCatMessage msg : msgList) {
				logCatBuffer.add( msg.toString() );
			}
		}
	};
	
	/**
	 * 저장된 LogCat 메세지들을 반환합니다. 
	 * 
	 * @return
	 */
	public List<String> getLogCatMessages() {
		return logCatBuffer.getItems();
	}
}
