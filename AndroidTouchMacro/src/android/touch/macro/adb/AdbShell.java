package android.touch.macro.adb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.touch.macro.G;
import android.touch.macro.util.Log;

/**
 * adb shell �� ����� �ܸ��⿡ ���������� ��ɾ ������ �� �ְ� �ϱ� ���� CLASS
 * 
 * @author MY
 *
 */
public class AdbShell {
	protected Process process = null;
	protected Thread  thread = null;
	private BufferedReader reader = null; 
	private BufferedWriter writer = null;
	private AdbShellCallback callback = null;
	
	/**
	 * @param deviceInfo 
	 * @return
	 */
	public synchronized boolean connect( DeviceInfo deviceInfo ) {
		close();
		
		ProcessBuilder builder = null;
		if( deviceInfo.serialNumber == null ) {
			builder = new ProcessBuilder( G.getDefaultProperties().getProperty("ADB_PATH"), "shell" );
		} else {
			builder = new ProcessBuilder( G.getDefaultProperties().getProperty("ADB_PATH"), "-s", deviceInfo.serialNumber, "shell" );
		}
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
	
			reader = new BufferedReader(new InputStreamReader ( process.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter( process.getOutputStream()));
			
			if( reader != null && writer != null ) {
				thread = new Thread( readerRunnable );
				thread.start();				
			}
			
			G.AdbProcess.add( process );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return thread == null ? false : thread.isAlive();
		//return process != null;
	}
	
	/**
	 * 
	 */
	public synchronized void close() {
		Log.i( "AdbConnector::close ==> IN" );
		
		if( writer != null ) {
			try { writer.close(); } catch (IOException e) { e.printStackTrace(); }
		}
				
		if( process != null ) {
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			
			while( System.currentTimeMillis() - reader_block_time < 100 ) {
				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			}
			process.destroy();
		}
		
		if( reader != null ) {
			try { reader.close(); } catch ( Exception e) { e.printStackTrace(); }
		}
		
		reader = null;
		writer = null;
		process = null;
		
		Log.i( "AdbConnector::close ==> OUT" );
	}
	
	/**
	 * @param cmd
	 */
	public synchronized boolean command( String cmd, AdbShellCallback callback ) {
		if( thread == null ? true : !thread.isAlive() ) {
			return false;
		}
		
		this.callback = callback;
		try {
			Log.i( "adb shell command : %s", cmd );
			writer.write( cmd + "\r\n" );
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	private long reader_block_time = 0;
	private Runnable readerRunnable = new Runnable() {
		public void run() {
			Log.i( "AdbConnector::run ==> IN" );
			
			String line;
			try {
				while ((line = reader.readLine ()) != null) {
					//Log.i( line );
					
					if( callback != null ) {
						callback.callback( AdbShell.this, line.trim() );
					}
					reader_block_time = System.currentTimeMillis();
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			Log.i( "AdbConnector::run ==> OUT" );
		}
	};
}
