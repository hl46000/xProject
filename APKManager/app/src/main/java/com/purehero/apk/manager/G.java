package com.purehero.apk.manager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;

public class G {
	public static final String DATE_FORMAT = "MM/dd/yy H:mm a";
	
	/**
	 * @param closeable
	 */
	public static void safe_close( Closeable closeable ) {
		if( closeable != null ) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param msg
	 */
	public static void log( String msg ) {
		Log.d( "ApkManager", msg );
	}
	
	/**
	 * 파일을 오픈하여 내용을 읽어서 문자열로 반환한다. 
	 * 
	 * @param file
	 * @return
	 */
	public static String readFile( File file ) {
		String result = null;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file );
			
			byte buffer[] = new byte[ (int) file.length() ];
			int nRead = fis.read( buffer, 0, buffer.length );
			
			result = new String( buffer, 0, nRead );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			safe_close( fis );
		}
		
		return result;
	}
	
	/**
	 * 문자열을 입력 받아 파일로 기록한다.
	 * 
	 * @param file
	 * @param content
	 * @return
	 */
	public static int writeFile( File file, String content ) {
		int result = -1;
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( file );
			
			byte buffer[] = content.getBytes("UTF-8");
			fos.write( buffer, 0, buffer.length );
			
			result = buffer.length;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			safe_close( fos );
		}
		
		return result;
	}
	
	/**
	 * 파일의 크기를 문자열로 반환한다. 
	 * 
	 * @param file
	 * @return
	 */
	public static String getFilesize( File file ) {
		String result = "0 B";
		
		float size = file.length();
		if( size < 1024.0f ) {
			result = String.format( "%.2f B", size );
		} else {
			size /= 1024.0f; 
			if( size < 1024.0f ) {
				result = String.format( "%.2f KB", size );
			} else {
				size /= 1024.0f; 
				if( size < 1024.0f ) {
					result = String.format( "%.2f MB", size );
				} else {
					size /= 1024.0f; 
					result = String.format( "%.2f GB", size );						
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Drawable 객체의  bitmap 객체를 반환합니다.
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap( BitmapDrawable drawable ) {
		//log( "BitmapDrawableToBitmap" );
		return drawable.getBitmap();
	}
	
	/**
	 * Drawable 객체의  bitmap 객체를 반환합니다.  
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap( PictureDrawable drawable ) {
		//log( "PictureDrawableToBitmap" );
		
		Bitmap bm = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bm);
	    canvas.drawPicture( drawable.getPicture());
	    return bm;
	}
	
	/**
	 * Drawable 객체의  bitmap 객체를 반환합니다.<br>
	 * 변환에 실패하면 null 을 반환한다. 
	 * 
	 * @param drawable
	 * @return Bitmap object or null
	 */
	public static Bitmap drawableToBitmap( Drawable drawable ) {
		if( drawable instanceof BitmapDrawable ) {
			return drawableToBitmap((BitmapDrawable) drawable );
		} else if( drawable instanceof PictureDrawable ) {
			return drawableToBitmap((PictureDrawable) drawable );
		}
		
		return null;
	}
	
	
	/**
	 * bitmap 객체를 Drawable 객체로 변환한다. 
	 * 
	 * @param context
	 * @param bitmap
	 * @return Drawable object
	 */
	public static Drawable bitmapToDrawable( Context context, Bitmap bitmap ) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}
	
	/**
	 * BITMAP 데이터를 PNG 파일로 저장 한다. 
	 * 
	 * @param bmp
	 * @param file
	 * @return
	 */
	public static boolean saveBitmapToFile( Bitmap bmp, File file ) {
		if( bmp == null ) return false;
		
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		    outStream.flush();		    
		    
		    return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		
		} catch ( Exception e ) {
			e.printStackTrace();
			
		} finally {
			if( outStream != null ) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	    return false;
	}
}
