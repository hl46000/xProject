package com.purehero.java.app00;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class LoadingTimeParser {

	public static void main(String[] args) {
		args = new String[] {
			"x:\\workTemp\\talkingtom_sealed",
			"com.outfit7.mytalkingtomfree",
			"x:\\workTemp\\result_tom.csv",
		};
		
		mainImpl( args );
	}

	private static void mainImpl( String [] args ) {
		File folder 		= new File( args[0] );		// �α׸� �˻��� ���� ���
		String packageName 	= args[1];					// package name
		File outFile		= new File( args[2] );		// ����� ������ ���� ���
		
		System.out.println();
		System.out.println("==> Start log parsing");
		System.out.println();
		
		Map<String,Vector<Long>> result = null;
		if( folder.exists() && folder.isDirectory()) {
			result = new LoadingTimeParser().run( folder, packageName );
		}
		
		System.out.println();
		System.out.println( String.format( "==> End log parsed" ));
		System.out.println();
		System.out.println();
		
		FileWriter fw = null;
		try {
			fw = new FileWriter( outFile );
			
			// ����� ���Ϸ� ����Ѵ�. 
			Set<String> keys = result.keySet();
			for( String key : keys ) {
				fw.write( key );
								
				Vector<Long> values = result.get( key );
				for( Long value : values ) {
					fw.write( String.format( ",%d", value ) );					
				}
				
				fw.write( System.lineSeparator() );				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fw != null ) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private Map<String,Vector<Long>> run(File base_folder, String package_name ) {
		
		// ���� �� �Է¹��� ������ �������� ���� ���ϵ��� �˻� �Ѵ�. 
		Stack<File> folders = new Stack<File>();
		folders.add( base_folder );
		
		Map<String,Vector<Long>> result = new HashMap<String,Vector<Long>>(); 
		
		int number = 1;
		while( !folders.isEmpty()) {			
			File folder = folders.pop();
		
			// ���� �������� �������� �ܸ����� �𵨸��� �����Ѵ�. 
			// �ܸ����� �𵨸��� ������ '_' ���� ���������� ���ڿ��̴�.  
			String foldername = folder.getName();
			int nIdx = foldername.lastIndexOf("_");
			if( nIdx > 0 ) {
				foldername = foldername.substring( nIdx + 1 );
			}
			
			String line 		= null;
			String beginLine	= null;
			String endLine		= null;
			boolean foundBeginLine = false;
			
			// ������ ��� ������ �˻��Ѵ�. 
			File files[] = folder.listFiles();
			for( File file : files ) {
				
				// ���� ���� �̸� ���� ���ÿ� Push �ϰ� ���� ���Ϸ� �̵��Ѵ�.  
				if( file.isDirectory()) {
					folders.push( file );
					continue;
				}
				
				// ������ �� ���ξ� �о� �´�. 
				BufferedReader br = null;
				
				try {
					br = new BufferedReader( new FileReader( file ));
					
					foundBeginLine = false;
					while(( line = br.readLine() ) != null ) {
						
						// �ð� ������ ���� Line �� ã�´�. 
						if( !foundBeginLine ) {
							foundBeginLine = checkBeginLine( line, package_name );
							if( foundBeginLine ) {
								beginLine = line;
							}
							
						// �ð� ������   �� Line �� ã�´�. 
						} else if( checkEndLine( line )) {
							endLine = line;
							
							// �� �αװ��� �ð� ���� Ȯ���Ѵ�. 
							SimpleDateFormat transFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
							try {
								Date beginDate 	= transFormat.parse(beginLine.substring(0, 18));
								Date endDate 	= transFormat.parse(endLine.substring(0, 18));
								long diff 		= endDate.getTime() - beginDate.getTime();
							
								Vector<Long> values = result.get( foldername );
								if( values == null ) {
									values = new Vector<Long>();
									result.put( foldername, values );
								}
								values.add( diff );
								
								// �ϴ� �α׷� ��� ����
								System.out.println( String.format( "[%3d] %s %s[%s - %s] %d(ms)",  number++, foldername, file.getName(), beginLine.substring(0, 18), endLine.substring(0, 18), diff ));								
								
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
							break;
						}
					}
					
					// ����/�� Line �� ã�� ���� ���
					if( line == null ) {
						// � �������� �α׸� ����Ѵ�. 
						System.out.println( String.format( "[%3d] %s - %s",  number++, foldername, file.getName() ));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					
				} catch (IOException e) {
					e.printStackTrace();
					
				} finally {
					
					// ���� ��Ʈ���� Close �Ѵ�. 
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
		
		return result;
	}

	/**
	 * �ð��� �����ϱ� ���� ���� Line �� �����Ѵ�. <br>
	 * ���� ����� "I/ActivityManager" and "for activity " + package_name ���ڿ��� ���Ե� Line �̴�.
	 * 
	 * @param line
	 * @param package_name
	 * @return
	 */
	private boolean checkBeginLine( String line, String package_name ) {
		if( line.contains( "ActivityManager" ) && line.contains( "for activity " + package_name )) return true;
		if( line.contains( "[JAVA] AppSealingLoader ........................................... v1.1.0" )) return true;
		return false;
	}
	
	/**
	 * �ð��� �����ϱ� ����   �� Line �� �����Ѵ�. <br>
	 * ���� ����� ���� Line �� ������ ���� ���ʷ� "D/Unity" ���ڿ��� ���Ե� Line �̴�.
	 * 
	 * @param line
	 * @return
	 */
	private boolean checkEndLine(String line) {
		return line.contains( "Unity" );
	}
}
