package com.purehero.module.shell.filelistfragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ShellProcess implements Runnable {
	Process process 	= null;
	Thread thread 		= null;
	BufferedWriter bw 	= null;
	
	
	String last_command	= "";
	boolean done = false;
	
	List<String> result = new ArrayList<String>();
	
	public ShellProcess() {
		try {
			process = Runtime.getRuntime().exec("sh");
		} catch (IOException e) {		
			e.printStackTrace();
			return;
		}
		
		thread = new Thread(this);
		thread.start();
		
		bw = new BufferedWriter( new OutputStreamWriter( process.getOutputStream())) ;
	}

	public List<String> command( String cmd ) {
		if( bw == null ) return null;
		try {
			last_command = cmd;
			
			done = false;
			bw.write( String.format( "echo 'START %s'\n%s\necho 'END %s'\n", last_command, last_command, last_command ));
			bw.flush();
			
			while( !done ) {
				try {
					Thread.sleep( 100 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void release() {
		if( bw != null ) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if( process != null ) {
			process.destroy();
			
			process = null;
		}
		
		if( thread != null ) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		if( process == null ) return;
		BufferedReader br = new BufferedReader( new InputStreamReader( process.getInputStream())) ;
		
		String startTag = null;
		String endTag = null;
		
		String line = null;
		try {
			while(( line = br.readLine()) != null && process != null ) {
				if( startTag == null ) {
					startTag = String.format( "START %s", last_command );
					endTag 	 = String.format( "END %s", last_command );
				}
				if( line.compareTo( startTag ) == 0 ) {
					result.clear();
					
				} else if( line.compareTo( endTag ) == 0 ) {
					startTag = null;
					done = true;
					
				} else {
					result.add( line );
				}
			}
		} catch (Exception e) {				
			//e.printStackTrace();
			
		} finally {
			if( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
