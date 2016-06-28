import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;

import com.inka.smc.SmcProcess;
import com.inka.util.ApkBuild;
import com.inka.util.ApkSign;
import com.inka.util.ApkTool;
import com.inka.util.G;
import com.inka.util.NdkBuild;


public class MainClass {
	private final String VERSION = "1.0.0";
	
	/**
	 * 앱에대한 정보를 출력 합니다. 
	 */
	public void print_info() {
		G.log( "SMC Process[Version %s]", VERSION );
		G.log( "Copyright (c) 2015 Purehero. All rights reserved." );
	}
	
	public static boolean isElfFile( File file ) {
		byte magicValue[] = new byte[4];
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file );
			fis.read( magicValue, 0, 4 );
			
			if( magicValue[0] == 0x7F &&
				magicValue[1] == 0x45 &&
				magicValue[2] == 0x4C &&
				magicValue[3] == 0x46
			) return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if( fis != null ) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		MainClass main = new MainClass();
		main.print_info();
		
		//final File android_proj_path = new File("D:\\workTemp\\x\\AndroidSmcTest");
		final File android_proj_path = new File("D:\\workTemp\\x\\AndroidProbeTest");
		
		NdkBuild ndk_build = new NdkBuild();
		if( !ndk_build.build( android_proj_path, false )) {
			G.errLog( "ERROR : failed to ndk-build '%s'", android_proj_path.getAbsolutePath() );
			return;
		}
				
		ApkBuild apk_build = new ApkBuild(); 
		File apk_file = apk_build.build( android_proj_path );
		if( apk_file == null ) {
			G.errLog( "ERROR : failed to build '%s'", android_proj_path.getAbsolutePath() );
			return;
		}
				
		ApkTool apk_tool = new ApkTool();
		File decode_apk_folder = apk_tool.decode( apk_file );
		if( decode_apk_folder == null ) {
			G.errLog( "ERROR : failed to decode apk_tool '%s'", apk_file.getAbsolutePath() );
			return;
		}
		
		SmcProcess smc = new SmcProcess();
		
		Stack<File> libFolder = new Stack<File>();
		libFolder.add( new File( decode_apk_folder, "lib" ));
		
		while( !libFolder.isEmpty()) {
			File subFiles [] = libFolder.pop().listFiles(); 
			for( File file : subFiles ) {
				if( file.isDirectory()) {
					libFolder.add( file );
					continue;
				}
				if( isElfFile( file )) {
					try {
						smc.add( file );
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
				
		try {
			smc.process();
		} catch (IOException e) {
			e.printStackTrace();
			
			G.errLog( "ERROR : failed to process SMC '%s'", decode_apk_folder.getAbsolutePath() );
			return;
		}
		
		File build_apk_file = apk_tool.build( decode_apk_folder );
		if( build_apk_file == null ) {
			G.errLog( "ERROR : failed to build apk_tool '%s'", decode_apk_folder.getAbsolutePath() );
			return;
		}
		
		ApkSign apk_sign = new ApkSign();
		File signed_apk_file = apk_sign.sign( build_apk_file );
		if( signed_apk_file == null ) {
			G.errLog( "ERROR : failed to sign apk file '%s'", build_apk_file.getAbsolutePath() );
			return;
		}
	}
}
