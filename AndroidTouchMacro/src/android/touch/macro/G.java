package android.touch.macro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class G {
	/**
	 * Application 이 사용하는 기본 폴더를 반환한다. 
	 * 
	 * @return
	 */
	public static File getDefaultPath() {
		File file = new File( System.getProperty("user.home") + "/TouchMacro");
		if( !file.exists()) file.mkdirs();
		return file;
	}
	
	/**
	 * Application에서 사용하는 temp 폴더를 반환한다. 
	 * 
	 * @return
	 */
	public static File getTempPath() {
		File temp_folder = new File( getDefaultPath().getAbsolutePath() + "/tmp" );
		if( !temp_folder.exists()) temp_folder.mkdirs();
		return temp_folder;
	}
	
	/**
	 * Application 이 사용하는 기본 DB 파일명을 반환한다.
	 * 
	 * @return
	 */
	public static String getDefaultDBName() {
		return "AndroidTouchMacro.db";
	}
			
	
	static Properties defaultProperties = null;
	/**
	 * Application 에서 사용할 default properties 객체를 반환한다. 
	 * @return
	 */
	public static Properties getDefaultProperties() {
		if( defaultProperties == null ) {
			defaultProperties = new Properties();
			
			File file = new File( getDefaultPath() + "/AndroidTouchMacro.properties");
			if( file.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream( file );
					defaultProperties.load(fis);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if( fis != null ) {
							fis.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return defaultProperties;
	}
	
	/**
	 * Application 에서 사용할 default properties 객체를 저장합니다.  
	 */
	public static void saveDefaultProperties() {
		if( defaultProperties == null ) return;
		
		File file = new File( getDefaultPath() + "/AndroidTouchMacro.properties");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( file );
			defaultProperties.store(fos, "AndroidTouchMacro.properties");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if( fos != null ) {
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Vector<Process> AdbProcess = new Vector<Process>(); 
	public static Vector<Process> tempAdbProcess = new Vector<Process>();
}
