package com.example.androidprobetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class Util {
	/**
	 * 실행중인 Process 객체에서 PID 값을 반환합니다. 
	 * 
	 * @param process PID값을 얻고 싶은 Process 객체
	 * @return process의 PID값 or 실패 시 -1
	 */
	public static int GetProcessID(Process process) {
	    try {
	        Class<?> ProcessImpl = process.getClass();
	        Field field = ProcessImpl.getDeclaredField("pid");
	        field.setAccessible(true);
	        return field.getInt(process);
	    } catch ( Exception e) {
	    	return -1;
	    }
	}
	
	/**
	 * 실행중인 자신의 ProcessID 값을 반환합니다. 
	 * 
	 * @param context
	 * @return 자신의 PID값 or 실패 시 -1
	 */
	public static int GetProcessID( Context context ) {
		ActivityManager am = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
		
		for( ActivityManager.RunningAppProcessInfo info : pids ) {
			if( info.processName.equalsIgnoreCase( context.getPackageName() )) {
				return info.pid;
			}
		}
		
		return -1;
	}
	
	
	/**
	 * 파일의 퍼미션을 변경한다. 
	 * 
	 * @param path
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public static int ChangePermissons( File path, int mode) throws Exception {
		Class<?> fileUtils = Class.forName("android.os.FileUtils");
		Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
		
		return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
	}
	
	/**
	 * file 의 SHA-256 Hash 값을 반환 합니다.
	 * 
	 * @param file
	 * @param nLen file의 처음부터 nLen 길이까지의 hash 값을 구한다. ( < 0 은 값이면 file의 전체 길이를 대상으로 한다. )
	 * @return file 의 SHA-256 Hash 값 or 실패 시 null
	 */
	public static byte [] GetSha256Digest( File file, int nLen ) {
		byte [] ret = null;
		
		if( nLen <= 0 ) {
			nLen = (int) file.length();
		}
		
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			fis = new FileInputStream( file ); 
			
			byte [] buffer = new byte[ 102400 ];	// 100K
			int nRead = 0;
			
			int byteCount = nLen > 102400 ? 102400 : nLen; 
			while((nRead = fis.read( buffer, 0, byteCount )) > 0 && nLen > 0 ) {
				md.update( buffer, 0, nRead );
				
				nLen -= nRead;
				byteCount = nLen > 102400 ? 102400 : nLen;
			}
			
			ret = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fis != null ) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
}
