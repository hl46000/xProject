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
		File folder 		= new File( args[0] );		// 로그를 검색할 폴더 경로
		String packageName 	= args[1];					// package name
		File outFile		= new File( args[2] );		// 결과를 저장할 파일 경로
		
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
			
			// 결과를 파일로 기록한다. 
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
		
		// 실행 시 입력받은 폴더를 기준으로 하위 파일들을 검색 한다. 
		Stack<File> folders = new Stack<File>();
		folders.add( base_folder );
		
		Map<String,Vector<Long>> result = new HashMap<String,Vector<Long>>(); 
		
		int number = 1;
		while( !folders.isEmpty()) {			
			File folder = folders.pop();
		
			// 현재 폴더명을 기준으로 단말기의 모델명을 유추한다. 
			// 단말기의 모델명은 마지막 '_' 문자 다음부터의 문자열이다.  
			String foldername = folder.getName();
			int nIdx = foldername.lastIndexOf("_");
			if( nIdx > 0 ) {
				foldername = foldername.substring( nIdx + 1 );
			}
			
			String line 		= null;
			String beginLine	= null;
			String endLine		= null;
			boolean foundBeginLine = false;
			
			// 폴더내 모든 파일을 검색한다. 
			File files[] = folder.listFiles();
			for( File file : files ) {
				
				// 하위 폴더 이면 폴더 스택에 Push 하고 다음 파일로 이동한다.  
				if( file.isDirectory()) {
					folders.push( file );
					continue;
				}
				
				// 파일을 한 라인씩 읽어 온다. 
				BufferedReader br = null;
				
				try {
					br = new BufferedReader( new FileReader( file ));
					
					foundBeginLine = false;
					while(( line = br.readLine() ) != null ) {
						
						// 시간 측정의 시작 Line 을 찾는다. 
						if( !foundBeginLine ) {
							foundBeginLine = checkBeginLine( line, package_name );
							if( foundBeginLine ) {
								beginLine = line;
							}
							
						// 시간 측정의   끝 Line 을 찾는다. 
						} else if( checkEndLine( line )) {
							endLine = line;
							
							// 두 로그간의 시간 차를 확인한다. 
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
								
								// 일단 로그로 찍어 보자
								System.out.println( String.format( "[%3d] %s %s[%s - %s] %d(ms)",  number++, foldername, file.getName(), beginLine.substring(0, 18), endLine.substring(0, 18), diff ));								
								
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
							break;
						}
					}
					
					// 시작/끝 Line 을 찾지 못한 경우
					if( line == null ) {
						// 어떤 파일인지 로그를 출력한다. 
						System.out.println( String.format( "[%3d] %s - %s",  number++, foldername, file.getName() ));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					
				} catch (IOException e) {
					e.printStackTrace();
					
				} finally {
					
					// 파일 스트림을 Close 한다. 
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
	 * 시간을 측정하기 위한 시작 Line 을 선별한다. <br>
	 * 선별 방법은 "I/ActivityManager" and "for activity " + package_name 문자열이 포함된 Line 이다.
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
	 * 시간을 측정하기 위한   끝 Line 을 선별한다. <br>
	 * 선별 방법은 시작 Line 을 나오고 나서 최초로 "D/Unity" 문자열이 포함된 Line 이다.
	 * 
	 * @param line
	 * @return
	 */
	private boolean checkEndLine(String line) {
		return line.contains( "Unity" );
	}
}
