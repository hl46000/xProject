package com.purehero.common.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageUtils {
	
	/**
	 * BufferedImage 의 크기를 확대/축소 하여 반환한다. 
	 * 
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
	
	/**
	 * Rotates an image. Actually rotates a new copy of the image.
	 * 
	 * @param img
	 * @param angle
	 * @return
	 */
	public static Image rotate( Image img, double angle ){
		if( angle == 0.0 ) return img;
		
		BufferedImage image = SwingFXUtils.fromFXImage( img, null );
		image = rotate( image, angle );
		return SwingFXUtils.toFXImage( image, null);
	}
}
