package com.purehero.java.app00;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * 
 * Google 에서 지원하는 단말기 목록파일의 크기를 줄이기 위한 Class
 * 
 * */
public class ParserSupportedDevices {

	public static void main(String[] args) {
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		File inputFile 	= new File("e:\\workTemp\\supported_devices.csv");
		File outputFile = new File( inputFile.getParentFile(), inputFile.getName().replace( ".csv", "_out.csv") );
		try {
			isr = new InputStreamReader( new FileInputStream( inputFile ), "UTF-8");
			osw = new OutputStreamWriter( new FileOutputStream( outputFile ), "UTF-8");
			
			br = new BufferedReader( isr ) ;
			bw = new BufferedWriter( osw ) ;
			
			Map<String,Map<String,String>> data = new HashMap<String,Map<String,String>>();
			
			String line;
			while(( line = br.readLine()) != null ) {
				String token[] = line.split(",");
				
				if( token[0] == null ) continue;
				if( token[0].length() < 2 ) continue;
				
				Map<String,String> name = data.get( token[0] );
				if( name == null ) {
					Map<String,String> a = new HashMap<String,String>();
					if( token[3] != null ) {
						a.put( token[1], token[3]);
						data.put( token[0], a );
					}
					
					continue;
				}
				
				String models = name.get( token[1] );
				if( models == null ) {
					name.put( token[1], token[3]  );
				} else {
					models = String.format( "%s%s", models, token[3] );
					name.put( token[1], models );
				}
			}
			
			Set<String> data_keys = data.keySet();
			for( String d_key : data_keys ) {
				Map<String,String> name = data.get( d_key );
				
				Set<String> name_keys = name.keySet();
				for( String n_key : name_keys ) {
					String model = name.get( n_key );
										
					bw.write( String.format( "%s,%s,%s", d_key, n_key, model ) );
					bw.newLine();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			graceful_close( br );
			graceful_close( isr );
			graceful_close( bw );
			graceful_close( osw );
		}
	}
	
	public static void graceful_close( Closeable obj ) {
		if( obj != null ) {
			try {
				obj.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
