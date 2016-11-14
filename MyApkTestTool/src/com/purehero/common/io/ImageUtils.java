package com.purehero.common.io;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageUtils {
	
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
	public static BufferedImage rotate( BufferedImage img, double angle ){
		if( angle == 0.0 || angle == 360.0 ) return img;
		
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
	
	public static Image rotate( Image img, double angle ){
		if( angle == 0.0 ) return img;
		
		BufferedImage image = SwingFXUtils.fromFXImage( img, null );
		image = rotate( image, angle );
		return SwingFXUtils.toFXImage( image, null);
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
}
