package com.purehero.atm.v3.model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UtilV3 {
	
	/**
	 * 
	 * 
	 * @param runnableClass
	 * @return
	 */
	public static String GetCurrentPath( Object runnableClass ) {
		String jarDir = runnableClass.getClass().getClassLoader().getResource("").getPath();
		if( System.getProperty( "os.name" ).contains( "Window" )) {
			if( jarDir.startsWith("/")) jarDir = jarDir.substring(1);
		}
		
		return jarDir;
	}
	
	/**
	 * @return
	 */
	public static String GetUserHomePath() {
		String home = System.getProperty("user.home");
		return home;
	}
	
	
	/**
	 * @param originalImage
	 * @param destWidth
	 * @param destHeight
	 * @return
	 */
	public static BufferedImage resizeImage( BufferedImage originalImage, int destWidth, int destHeight ) {
		BufferedImage resizedImage = new BufferedImage( destWidth, destHeight, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, destWidth, destHeight, null);
		g.dispose();
	 
		return resizedImage;
	}
	
	
	/**
	 * Rotates an image. Actually rotates a new copy of the image.
	 * 
	 * @param img The image to be rotated
	 * @param angle The angle in degrees
	 * @return The rotated image
	 */
	public static BufferedImage rotate(BufferedImage img, double angle)	{
		if( angle == 0.0 ) return img;
		
		double sin = Math.abs(Math.sin(Math.toRadians(angle)));
		double cos = Math.abs(Math.cos(Math.toRadians(angle)));

	    int w = img.getWidth(null), h = img.getHeight(null);

	    int neww = (int) Math.floor(w*cos + h*sin);
	    int newh = (int) Math.floor(h*cos + w*sin);

	    BufferedImage bimg = new BufferedImage( neww, newh, img.getType());
	    Graphics2D g = bimg.createGraphics();

	    g.translate((neww-w)/2, (newh-h)/2);
	    g.rotate(Math.toRadians(angle), w/2, h/2);
	    g.drawRenderedImage(img, null);
	    g.dispose();

	    //img.flush();
	    //img = null;
	    
	    return bimg;
	}
	
	/**
	 * ?��?��?�� 좌표값을 ?��면좌?��값을 ?��면에 ?��?��?�� ?��??/축소값을 ?��?��?��?�� ?��?��?�� X,Y 값을 반환 ?��?��?��. <br>�?, ?��면좌?��?�� ?��?�� 받아 ?��바이?��?�� 좌표�? �?경시켜반?��?��?��?��.
	 * 
	 * @param pt
	 * @param ratio
	 * @return
	 */
	public static Point getRatioedPoint(Point pt, double ratio) {
		return new Point((int)(pt.x/ratio), (int)(pt.y/ratio));
	}
	
	
	/**
	 * ?��바이?�� 좌표�? ?��?��받아 ?���? 좌표값의 비율�? 조정?�� 값을 반환?��?��?��. 
	 * 
	 * @param pt
	 * @param ratio
	 * @return
	 */
	public static Point getUnratioedPoint(Point pt, double ratio) {
		return new Point((int)(pt.x*ratio), (int)(pt.y*ratio));
	}
	
	
	/**
	 * ?��미�??�� angle ?�� ?��?��?�� 좌표�? �??��?��?�� 반환 ?��?��?��. 
	 * 
	 * @param pt
	 * @param width
	 * @param height
	 * @param angle
	 * @return
	 */
	public static Point getAngledPoint( Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.x = pt.y; ret.y = height - pt.x;
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = width - pt.y; ret.y = pt.x;
		}
		
		return ret;
	}
	
	/**
	 * ?��미�??�� angle ?�� ?��?��?�� 좌표�? �??��?��?�� 반환 ?��?��?��.
	 * 
	 * @param pt
	 * @param width
	 * @param height
	 * @param angle
	 * @return
	 */
	public static Point getUnangledPoint(Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.y = pt.x; ret.x = width - pt.y; 
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = pt.y; ret.y = height - pt.x;
		}
		
		return ret;
	}
	
	/**
	 * @param prog
	 * @return
	 */
	@SuppressWarnings("resource")
	public static List<String> getRuntimeExecResult( String prog, CallbackMessage callback ) {
		InputStream input = null;
		InputStream error = null;
		Process process = null;
		
		List<String> ret = new ArrayList<String>();
		try {
			process = Runtime.getRuntime().exec( prog );
			
			input = process.getInputStream();
			error = process.getErrorStream();
		
			String line;
			Scanner input_scaner = new Scanner(input).useDelimiter("\\n");
			while( input_scaner.hasNext() ) {
				line = input_scaner.next();
			
				ret.add( line );
				if( callback != null ) {
					callback.callbackMessage(line);
				}
			}
			
			Scanner error_scaner = new Scanner(error).useDelimiter("\\n");
			while( error_scaner.hasNext() ) {
				line = error_scaner.next();

				ret.add( line );
				if( callback != null ) {
					callback.callbackMessage(line);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if( error != null ) {
				try {
					error.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	
	/**
	 * Alert 창의 ?��?��?��. 
	 * 
	 * @param title
	 * @param message
	 * @param type
	 */
	/*
	public static void alertWindow( String title, String message, AlertType type ) {
		Alert alert = new Alert( type );
		alert.setTitle( title );
		alert.setHeaderText(null);
		alert.setContentText( message );
		
		alert.showAndWait();
	}
	*/

	/**
	 * is 의 스트림을 outFile 로 기록합니다. <br> 기록이 완료되면 is 스트림은 close 시킴니다.
	 *  
	 * @param is
	 * @param outFile
	 * @return outFile 에 기록된 byte 수를 반환 합니다. 
	 */
	public static int fileWrite(InputStream is, File outFile) {
		int ret = 0;
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( outFile );
			
			byte buffer[] = new byte[102400];
			int nRead = 0;
			
			while(( nRead = is.read( buffer )) > 0 ) {
				ret += nRead;
				
				fos.write( buffer, 0, nRead );
			}
						
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				if( fos != null ) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
