package com.example.androidprobetest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class ProbeMainActivity extends Activity {
	private static final String LOG_TAG = "probe_java";
	private static final String PROBE_FILENAME = "myProbe";
	private ServerSocket server = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			server = new ServerSocket(0);
			new Thread( server_socket_runnable ).start();
			
			// probe 을 실행 시키면서 전달할 파라메터 데이터를 생성합니다. 
			ArrayList<String> params = new ArrayList<String>();
			params.add( String.valueOf( server.getLocalPort() ));
			
			int probePID = runProbe( params );
			Log.d( LOG_TAG, String.format( "probePID = %d", probePID ));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		if( server != null ) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		getLocalProbeFile().delete();
		
		super.onDestroy();
	}
	
	
	private Runnable server_socket_runnable = new Runnable() {
		@Override
		public void run() {
			try {
				Socket client = server.accept();
				Log.d( LOG_TAG, "Accepted client socket" );
				
				if( check_probe_hash( client.getInputStream() ) < 1 ) {
					//return;
				}
				
				run_lua_script( client.getOutputStream() );
				
				client.close();
				Log.d( LOG_TAG, "Closed client socket" );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@SuppressLint("SdCardPath")
		private void run_lua_script( OutputStream ops ) throws IOException {
			int currentPID = Util.GetProcessID( ProbeMainActivity.this );
			
			byte read_buff[] = new byte[1024];
			
			InputStream is = null;
			try {
				is = new FileInputStream( new File( "/sdcard/download/myProbe.lua"));
			} catch( Exception e ) {}
			
			if( is == null ) {
				is = ProbeMainActivity.this.getAssets().open( "script/test.lua" );
			}
			//InputStream is = MainActivity.this.getAssets().open( "script/luac.out" );
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
						
			int nRead = 0;
			while(( nRead = is.read( read_buff, 0, 1024)) > 0 ) {
				baos.write( read_buff, 0, nRead );
			}
			String fileContent = new String( baos.toByteArray() );
			fileContent = fileContent.replace( "@@MAIN_PID", String.valueOf(currentPID));
			
			Log.d( LOG_TAG, "====================================" );
			Log.d( LOG_TAG, fileContent );
			Log.d( LOG_TAG, "====================================" );
			
			byte [] writeDatas = fileContent.getBytes( Charset.forName("UTF-8"));
			int len = writeDatas.length;
			
			String strLen = String.format( Locale.getDefault(), "%10d", len );
			byte [] writeLenDatas = strLen.getBytes( Charset.forName( "UTF-8" ));
			
			Log.d( LOG_TAG, String.format( Locale.getDefault(), "writeLenDatas length : %d", writeLenDatas.length ));
						
			ops.write( writeLenDatas, 0, writeLenDatas.length ); 	Log.d( LOG_TAG, "writted writeLenDatas" );
			ops.write( writeDatas, 0, writeDatas.length );			Log.d( LOG_TAG, "writted writeDatas" );			
		}

		private int check_probe_hash( InputStream is ) throws IOException {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return -1;
			}
			
			byte recv_buff [] = new byte[102400];	// 100Kbyte
			is.read( recv_buff, 0, 10 );
			
			// 아래 길이만큼의 prove 파일의 hash 검증을 진행 한다. 
			int probe_check_len = Integer.valueOf( new String( recv_buff, 0, 10 ).trim());
			Log.d( LOG_TAG, String.format( "check probe size : %d", probe_check_len ) );
			
			byte [] probeHash1 = Util.GetSha256Digest( getLocalProbeFile(), probe_check_len );
			
			int nRead, nTotal = 0;
			while( probe_check_len > 0 ) {
				nRead = is.read( recv_buff, 0, Math.min( probe_check_len, 102400 ));
				
				md.update( recv_buff, 0, nRead );
				
				nTotal += nRead;
				probe_check_len -= nRead;
				
				Log.d( LOG_TAG, String.format( "Recieved data : %dbyte", nRead ) );
			}
			Log.d( LOG_TAG, String.format( "Total recieved data : %dbyte", nTotal ) );
			
			byte [] probeHash2 = md.digest();
			if( !Arrays.equals( probeHash1, probeHash2 )) {
				Log.e( LOG_TAG, "probe hash not matched!!" );
				return -1;
			}
			Log.d( LOG_TAG, "probe hash matched!!" );
			
			return nTotal;
		}
	};
	
	
	/**
	 * probe의 파일이 위치할 File 객체를 반환한다. 
	 * 
	 * @return
	 */
	private File getLocalProbeFile() {
		return getFileStreamPath( PROBE_FILENAME );
	}
	
	/**
	 * probe 파일을 assets 에서 app 영역으로 복사하고 그 파일의 크기를 반환한다. 
	 * 
	 * @return
	 */
	private int extractProbe( String srcFilename, File dstFile ) {
		Log.d( LOG_TAG, "ExtractProbe()" );
		
		String arch = System.getProperty("os.arch");
		arch = arch.toLowerCase( Locale.getDefault());
		
		if( arch.startsWith("arm")) 	 arch = "armeabi";
		else if( arch.startsWith("x86")) arch = "x86";
		else arch = Build.CPU_ABI;
		
		String assets_probe = String.format( "probe/%s/%s", arch, srcFilename );
		Log.d( LOG_TAG, String.format( "path : %s", assets_probe ));
		
		int ret = 0;
		try {
			InputStream is = this.getAssets().open( assets_probe );
			FileOutputStream fos = new FileOutputStream( dstFile );

			byte buff [] = new byte[1024];
			
			int nRead = 0;
			while(( nRead = is.read( buff, 0, 1024)) > 0 ) {
				fos.write( buff, 0, nRead );
				ret += nRead;
			}
			
			is.close();
			fos.close();
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 새로운 Thread 을 생성하여 process 가 종료될때가지 기다려서 로그 및 오류 내용을 출력 합니다. 
	 * 
	 * @param process
	 */
	private void readProcess( final Process process ) {
		new Thread( new Runnable(){

			@Override
			public void run() {
				byte buff[] = new byte[1024];
				
				InputStream is = process.getInputStream();
				try {
					int nRead = is.read( buff, 0, 1024 );
								
					if( nRead > 0 ) {
						String msg = new String( buff, 0, nRead );
						Log.d( LOG_TAG, msg );
					}
					
					is = process.getErrorStream();
					nRead = is.read( buff, 0, 1024 );
								
					if( nRead > 0 ) {
						String msg = new String( buff, 0, nRead );
						Log.d( LOG_TAG, msg );
					}
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}}).start();
	}
	
	
	/**
	 * probe 을 실행 시키고 probe의 PID 값을 반환합니다. <br>현재 x86/armeabi 을 지원합니다. 
	 * 
	 * @param params probe을 실행 시킬 때 전달할 파라메터 Array 값 
	 * @return 실행된 probe의 PID 값 or 실패 시 -1
	 */
	@SuppressLint("SdCardPath")
	private int runProbe( ArrayList<String> params ) {
    	Log.d( LOG_TAG, "runProbe() <<" );
    	
    	File probe_file = this.getFileStreamPath( PROBE_FILENAME );
    	extractProbe( "probe", probe_file );
    	try {
			Util.ChangePermissons( probe_file, 0777 );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	
    	File tool_file = this.getFileStreamPath("test_tool");//new File( "/sdcard/download/test_tool" );
    	extractProbe( "test_tool", tool_file );
    	try {
    		Util.ChangePermissons( tool_file, 0777 );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	    	
		params.add( 0, getLocalProbeFile().getAbsolutePath());
		params.add( "&");
		int pID = -1;
		
        try {
        	//params.add( 0, "-c");
        	//params.add( 0, "su");
        	
        	String [] progArray = params.toArray( new String[params.size()] );
			Process process = Runtime.getRuntime().exec( progArray );
			
			pID = Util.GetProcessID( process );
			
			readProcess( process );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        Log.d( LOG_TAG, "runProbe() >>" );
        return pID;
	}
	
	
}
