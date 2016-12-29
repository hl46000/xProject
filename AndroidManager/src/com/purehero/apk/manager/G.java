package com.purehero.apk.manager;

import java.io.Closeable;
import java.io.File;
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
	 * Drawable 객체의  bitmap 객체를 반환합니다.
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap( BitmapDrawable drawable ) {
		log( "BitmapDrawableToBitmap" );
		return drawable.getBitmap();
	}
	
	/**
	 * Drawable 객체의  bitmap 객체를 반환합니다.  
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap( PictureDrawable drawable ) {
		log( "PictureDrawableToBitmap" );
		
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
