package com.inka.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * ALS-542 
 * 
 * 폴더 안에 모여 있는 APK 파일들에게서 package name 만을 빼와서 파일로 기록해 주는 툴입니다.
 * <package name>,<package name>,<package name>,<package name>,<package name> 형식으로 파일이 생성됨
 * 
 * 
 * [사용 방법]
 * java -jar <IN:PackageNameExtractor.jar 전체경로> <IN:aapt.exe 전체경로> <IN:APK 파일이 가득한 폴더 전체경로> <OUT:package_names.txt 전체경로>
 * 
 * */

public class Tool_ExtractionPackageNames {
	public static void main(String[] args) {
		if( args.length < 3 ) {
			System.out.printf( "Usage : jvar -jar <this jar file> aapt.exe <target folder> <result file>\n" );
			return;
		}
	
		File aapt = new File( args[0] );
		if( !aapt.exists() ) {
			System.out.printf( "'%s' file not exist\n", aapt.getAbsolutePath() );
			return ;
		}
		if( aapt.getName().compareTo( "aapt.exe") != 0 ) {
			System.out.printf( "'%s' file name not aapt.exe\n", aapt.getAbsolutePath() );
			return ;
		}
		
		System.out.printf( "AAPT.EXE file path : %s\n", aapt.getAbsolutePath() );
		
		File target_path = new File( args[1] );
		if( !target_path.exists()) {
			System.out.printf( "'%s' folder not exist\n", target_path.getAbsolutePath() );
			return;
		}
		
		System.out.printf( "TARGET FOLDER path : %s\n", target_path.getAbsolutePath() );
				
		Map<String,String> uniq_package_name = new HashMap<String,String>();
		
		File files[] = target_path.listFiles();
		for( File file : files ) {
			if( !file.getName().endsWith( ".apk" )) continue;
			
			System.out.printf( "Process file : %s, ", file.getName());
			
			String command = String.format( "%s@>>--dump@>>--badging@>>--%s", aapt.getAbsolutePath(), file.getAbsolutePath());
			try {
				ProcessBuilder builder = new ProcessBuilder( command.split("@>>--") );
				builder.redirectErrorStream(true);
				Process process = builder.start();
				
				InputStreamReader istream = new  InputStreamReader(process.getInputStream(), Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(istream);
				
				//process.waitFor();
				
				String package_name = null;
				String game_name	= null;
				
				String line;
				while ((line = br.readLine()) != null) {
					Thread.sleep( 100 );
					
					line = line.replace("%", "%%");
					if( line.startsWith( "package: name=" )) {
						
						String token1 = line.substring( "package: name=".length());
						String token2[] = token1.split( " " );
						
						package_name = token2[0].replace( "\'", "").trim();
						System.out.printf( "package name : %s\n", package_name );
						
						if( game_name != null) {
							uniq_package_name.put( package_name, game_name );
							break;
						}
					} else if( line.startsWith( "application-label:" )) {
						String token1 = line.substring( "application-label:".length());
												
						game_name = token1.replace( "\'", "").trim();
						System.out.printf( "game name : %s\n", game_name );
						
						if( package_name != null) {
							uniq_package_name.put( package_name, game_name );
							break;
						}
					}
				}
				
				br.close();

			} catch( Exception e ) {
				e.printStackTrace();
			}
			
		}
		
		if( uniq_package_name.size() > 0 ) {
			File out_file = new File( args[2] );
			FileOutputStream fos = null;
			
			try {
				fos = new FileOutputStream( out_file );
				boolean bFirst = true;
				
				Set<String> keys = uniq_package_name.keySet();
				for( String key : keys ) {
					String value = uniq_package_name.get(key);
					
					String write_data = String.format( "%s:%s", key, value );
					byte [] datas = write_data.getBytes();
					
					if( bFirst ) {
						bFirst = !bFirst;
					} else {
						byte [] comma = ",".getBytes();
						fos.write( comma, 0, comma.length );
					}
					fos.write( datas, 0, datas.length);
				}				
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
				
			} finally {
				if( fos != null ) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
