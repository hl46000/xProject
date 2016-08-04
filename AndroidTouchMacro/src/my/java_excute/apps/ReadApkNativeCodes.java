package my.java_excute.apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadApkNativeCodes {
	public static void main(String[] args) {
		if( args.length < 2 ) {
			System.out.println( "USAGE : java -jar <this jar file> <APKS_PATH> <OUTPUT_FILE>" );
			return;
		}
		
		try {
			new ReadApkNativeCodes().Run( args );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void Run( String[] args ) throws IOException {
		File path = new File( args[0] );
		if( !path.exists()) return;
		
		FileOutputStream fos = new FileOutputStream( args[1]);
		
		File files[] = path.listFiles();
		for( File file : files ) {
			if( !file.getName().toLowerCase().endsWith(".apk")) continue;
			
			try {
				getApkNativeCodes( file, fos );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		fos.close();
	}
	
	void getApkNativeCodes( File file, FileOutputStream fos ) throws IOException, InterruptedException {
		String command = String.format( "cmd@>>--/c@>>--aapt.exe@>>--dump@>>--badging@>>--%s", file.getAbsolutePath() );
	
				
		ProcessBuilder builder = new ProcessBuilder( command.split("@>>--") );
		builder.redirectErrorStream(true);
		Process process = builder.start();
	
		String ret = null;
	
		InputStreamReader istream = new  InputStreamReader(process.getInputStream());
		BufferedReader br = new BufferedReader(istream);
				
		String line;
		while ((line = br.readLine()) != null ){
			if( ret == null ) {
				ret = line;
			} else {
				ret += "\n";
				ret += line;
			}
										
			if( line.startsWith( "native-code" )) {
				line = line.substring( "native-code: ".length());
				line = line.replace( "'", "");
				
				if( fos != null ) {
					String output_msg = String.format( "%s : %s\r\n", file.getName(), line );
					fos.write( output_msg.getBytes());
				}
				
			}
		}
		
		process.waitFor();
		br.close();
	}
}
